type ide =	string;;
type exp =	Eint of int 
			| Ebool of bool
			| Den of ide
			| Prod of exp * exp
			| Sum of exp * exp
			| Diff of exp * exp
			| Eq of exp * exp
			| Minus of exp
			| IsZero of exp
			| Or of exp * exp
			| And of exp * exp
			| Not of exp
			| Ifthenelse of exp * exp * exp
			| Let of ide * exp * exp
			| Fun of ide * exp
			| FunCall of exp * exp
			| Letrec of ide * exp * exp
			| DictInit of (ide * exp) list (*Espressione che rappresenta la dichiarazione di un Dizionario*)
			| DictCall of exp * ide (*Espressione che rappresenta la chiamata di un argomento*)
			| DictRemove of exp * ide (*Espressione che restituisce un dizionario con l'elemento rappresentato da ide rimosso*) 
			| DictAdd of exp * (ide * exp) (*Espressione che restituisce un dizionario con l'elemento (ide * exp) aggiunto *)
			| DictApplyOver of exp * exp;; (*Espressione che esegue una funzione su tutti gli elementi del dizionario*)

(*ambiente polimorfo*)
type 't env = ide -> 't;;

let emptyenv (v : 't) = function x -> v;;
let applyenv (r : 't env) (i : ide) = r i;;
let bind (r : 't env) (i : ide) (v : 't) = function x -> if x = i then v else applyenv r x;;

let emptydict (v : 't) = function x -> v;; (*Crea un dizionario vuoto*)
let applydict (d : 't env) (i : ide) = d i;; (*Richiama la funzione dizionario e ne restituisce il valore corrispondente ad ide*)
let binddict (d : 't env) (n : ide) (v : 't) = function x -> if x = n then v else applydict d x;; (*Associa un valore v alla chiave n sul dizionario d*)

(*tipi esprimibili*)
type evT =	Int of int
			| Bool of bool
			| Unbound
			| DictVal of (ide -> evT) (*il dizionario è una funzione che da Ide restituisce un altro valore dell'ambiente*)
			| FunVal of evFun
			| DinFunVal of evFund (*Valore che identifica una funzione dinamica all'interno dell'ambiente*)
			| RecFunVal of ide * evFun 
			| DinRecFunVal of ide * evFund (*Valore che identifica una funzione ricorsiva dinamica all'interno dell'ambiente*)
			and evFun = ide * exp * evT env
			and evFund = ide * exp (*Ambiente delle funzioni sotto scoping dinamico, l'abiente al tempo di chiamata della funzione non è presente*)

(*rts*)
(*type checking*)
let typecheck (s : string) (v : evT) : bool = match s with
	"int" -> (match v with
		Int(_) -> true
		| _ -> false)
	| "bool" -> (match v with
		Bool(_) -> true
		| _ -> false)
	| _ -> failwith("not a valid type");;

(*funzioni primitive*)
let prod x y = if (typecheck "int" x) && (typecheck "int" y)
	then (match (x,y) with
		(Int(n),Int(u)) -> Int(n*u))
	else failwith("Type error");;

let sum x y = if (typecheck "int" x) && (typecheck "int" y)
	then (match (x,y) with
		(Int(n),Int(u)) -> Int(n+u))
	else failwith("Type error");;

let diff x y = if (typecheck "int" x) && (typecheck "int" y)
	then (match (x,y) with
		(Int(n),Int(u)) -> Int(n-u))
	else failwith("Type error");;

let eq x y = if (typecheck "int" x) && (typecheck "int" y)
	then (match (x,y) with
		(Int(n),Int(u)) -> Bool(n=u))
	else failwith("Type error");;

let minus x = if (typecheck "int" x) 
	then (match x with
	   	Int(n) -> Int(-n))
	else failwith("Type error");;

let iszero x = if (typecheck "int" x)
	then (match x with
		Int(n) -> Bool(n=0))
	else failwith("Type error");;

let vel x y = if (typecheck "bool" x) && (typecheck "bool" y)
	then (match (x,y) with
		(Bool(b),Bool(e)) -> (Bool(b||e)))
	else failwith("Type error");;

let et x y = if (typecheck "bool" x) && (typecheck "bool" y)
	then (match (x,y) with
		(Bool(b),Bool(e)) -> Bool(b&&e))
	else failwith("Type error");;

let non x = if (typecheck "bool" x)
	then (match x with
		Bool(true) -> Bool(false)
		| Bool(false) -> Bool(true))
	else failwith("Type error");;

(*interprete*)
let rec eval (e : exp) (r : evT env) : evT = match e with
	Eint n -> Int n
	| Ebool b -> Bool b
	| IsZero a -> iszero (eval a r)
	| Den i -> applyenv r i
	| Eq(a, b) -> eq (eval a r) (eval b r)
	| Prod(a, b) -> prod (eval a r) (eval b r)
	| Sum(a, b) -> sum (eval a r) (eval b r)
	| Diff(a, b) -> diff (eval a r) (eval b r)
	| Minus a -> minus (eval a r)
	| And(a, b) -> et (eval a r) (eval b r)
	| Or(a, b) -> vel (eval a r) (eval b r)
	| Not a -> non (eval a r)
	| Ifthenelse(a, b, c) -> 
		let g = (eval a r) in
			if (typecheck "bool" g) 
				then (if g = Bool(true) then (eval b r) else (eval c r))
				else failwith ("nonboolean guard")
	| Let(i, e1, e2) -> eval e2 (bind r i (eval e1 r))
	| Fun(i, a) -> FunVal(i, a, r)
	| FunCall(f, eArg) -> 
		let fClosure = (eval f r) in
			(match fClosure with
				FunVal(arg, fBody, fDecEnv) -> 
					eval fBody (bind fDecEnv arg (eval eArg r))
				| RecFunVal(g, (arg, fBody, fDecEnv)) -> 
					let aVal = (eval eArg r) in
						let rEnv = (bind fDecEnv g fClosure) in
							let aEnv = (bind rEnv arg aVal) in
								eval fBody aEnv
				| _ -> failwith("non functional value"))
	| Letrec(f, funDef, letBody) ->
		(match funDef with
			Fun(i, fBody) -> 
				let r1 = (bind r f (RecFunVal(f, (i, fBody, r)))) in
							eval letBody r1
			| _ -> failwith("non functional def"))
	| DictInit (liscp) -> DictVal (let rec bland lcp =	(*Dobbiamo bindare al dizionario tutte le coppie chiave valori che ha*)
										match lcp with	(*Lo facciamo con Pattern Matching e funzione ricorsiva per scorrere tutta la lista*)
										[] -> emptydict Unbound
										| (xne, xnv)::[] -> binddict (emptydict Unbound) xne (eval xnv r)
										| (xne, xnv)::xl -> binddict (bland xl) xne (eval xnv r)
									in bland liscp)
	| DictCall (nomed, nomev) -> let f = eval nomed r in  (*Dobiamo valutare il nome del dizionario (una exp)*)
									(match f with
									DictVal (d) -> d nomev (*Otteniamo un DictVal, che è una funzione, e lo eseguiamo, in modo che restituisca un evT*)
									| _ -> failwith ("non dictionary value")) (*Se non è un dizionario è errore*)
	| DictRemove (nomed, nomev) -> DictVal (function x -> if x = nomev then Unbound else let f = eval nomed r in (*Nuova funzione dove se x = chiave resituisce Unbound*)
																							match f with
																							DictVal (d) -> d nomev
																							| _ -> failwith ("non dictionary value"))
	| DictAdd (nomed, (nomev, valv)) -> DictVal (function x -> if x = nomev then (eval valv r) else let f = eval nomed r in (*Nuova funzione dove se x = chiave resituisce il valore della chiave*)
																							match f with
																							DictVal (d) -> d nomev
																							| _ -> failwith ("non dictionary value"))
	| DictApplyOver (f, nomed) -> DictVal (function x -> let fv = eval nomed r in (*Valutiamo il dizionario, se non ci ritroviamo con un DictVal lanciamo una eccezione*)
														match fv with
														DictVal (d) -> if d x = Unbound then Unbound  (*Controlliamo che il valore abbia effettivamente un valore, in modo da evitare di applicare la funzione su valori insesistenti*)
																						else eval (FunCall (f, (DictCall (nomed, x)))) r) (*Valutiamo l'espressione come se richiamassimo la funzione f di parametro il valore associato alla chiave x nel dizionario*)
														| _ -> failwith ("non dictionary value");;

(*interprete dinamico*)
let rec rt_eval (e : exp) (r : evT env) : evT = match e with
	Eint n -> Int n
	| Ebool b -> Bool b
	| IsZero a -> iszero (eval a r)
	| Den i -> applyenv r i
	| Eq(a, b) -> eq (rt_eval a r) (rt_eval b r)
	| Prod(a, b) -> prod (rt_eval a r) (rt_eval b r)
	| Sum(a, b) -> sum (rt_eval a r) (rt_eval b r)
	| Diff(a, b) -> diff (rt_eval a r) (rt_eval b r)
	| Minus a -> minus (rt_eval a r)
	| And(a, b) -> et (rt_eval a r) (rt_eval b r)
	| Or(a, b) -> vel (rt_eval a r) (rt_eval b r)
	| Not a -> non (rt_eval a r)
	| Ifthenelse(a, b, c) -> 
		let g = (rt_eval a r) in
			if (typecheck "bool" g) 
				then (if g = Bool(true) then (rt_eval b r) else (rt_eval c r))
				else failwith ("nonboolean guard")
	| Let(i, e1, e2) -> rt_eval e2 (bind r i (rt_eval e1 r))
	| Fun(i, a) -> DinFunVal(i, a) (*La funzione ora ha il suo valore dinamico corrispettivo nell'ambiente*)
	| FunCall(f, eArg) -> 
		let fClosure = (rt_eval f r) in
			(match fClosure with
				DinFunVal(arg, fBody) -> 
					rt_eval fBody (bind r arg (rt_eval eArg r)) (*Valutiamo usando r come ambiente*)
				| DinRecFunVal(g, (arg, fBody)) -> 
					let aVal = (rt_eval eArg r) in
						let rEnv = (bind r g fClosure) in (*Ugualmente qui usiamo r*)
							let aEnv = (bind rEnv arg aVal) in
								rt_eval fBody aEnv
				| _ -> failwith("non functional value"))
	| Letrec(f, funDef, letBody) ->
		(match funDef with
			Fun(i, fBody) -> 
				let r1 = (bind r f (DinRecFunVal(f, (i, fBody)))) in (*Richiamavo il valore dinamico corrispettivo della funzione ricorsiva nell'ambiente*)
							rt_eval letBody r1
			| _ -> failwith("non functional def"))
	| DictInit (liscp) -> DictVal (let rec bland lcp =
										match lcp with
										(xne, xnv)::[] -> binddict (emptydict Unbound) xne (eval xnv r)
										| (xne, xnv)::xl -> binddict (bland xl) xne (eval xnv r)
									in bland liscp)
	| DictCall (nomed, nomev) -> let f = rt_eval nomed r in 
									(match f with
									DictVal (d) -> d nomev (*richiamo il valore associato a nomev nel dizionario nomed*)
									| _ -> failwith ("non dictionary value"))
	| DictRemove (nomed, nomev) -> DictVal (function x -> if x = nomev then Unbound else let f = rt_eval nomed r in
																							match f with
																							DictVal (d) -> d nomev
																							| _ -> failwith ("non dictionary value"))
	| DictAdd (nomed, (nomev, valv)) -> DictVal (function x -> if x = nomev then (eval valv r) else let f = rt_eval nomed r in
																							match f with
																							DictVal (d) -> d nomev
																							| _ -> failwith ("non dictionary value"))
	| DictApplyOver (f, nomed) -> DictVal (function x -> let fv = rt_eval nomed r in
														match fv with
														DictVal (d) -> if d x = Unbound then Unbound 
																						else rt_eval (FunCall (f, (DictCall (nomed, x)))) r)
														| _ -> failwith ("non dictionary value");;
		
(* =============================  TESTS  ================= *)

let print (x : evT) (s: string) = match x with
		Int(out) -> print_string(s);print_int(out);print_string("\n")
		| Bool(out) ->print_string(s); if out then print_string("true\n")
						else print_string("false\n")
		| Unbound ->print_string(s);print_string("Unbound\n");;

(*Test1 -> Creazione dizionario*)

(*let my_dict = {'a': 4, 'b': 1, 'c': 3} in
	my_dict['a']*)
(*ritorna 4*)

let env0 = emptyenv Unbound;;

let usage_let = DictCall(Den("my_dict"), "a");;
let first_let = Let("my_dict", DictInit([("a", Eint(4));("b", Eint(1));("c", Eint(3))]), usage_let);;

print (eval first_let env0) "my_dict['a']: ";; 

(*Test2 -> Rimozione elemento dizionario*)

(*let my_dict = {'a': 4, 'b': 1, 'c': 3} in
	let my_dict2 = rm('my_dict', 'a')
		my_dict2['a']*)
(*ritorna Unbound*)

let usage_let = DictCall(Den("my_dict2"), "a");;
let second_let = Let("my_dict2", DictRemove(Den("my_dict"), "a"), usage_let);;
let first_let = Let("my_dict", DictInit([("a", Eint(4));("b", Eint(1));("c", Eint(3))]), second_let);;

print (eval first_let env0) "my_dict['a']: ";;

(*Test2 -> Aggiunta elemento dizionario*)

(*let my_dict = {'a': 4, 'b': 1, 'c': 3} in
	let my_dict2 = my_dict['d'] = 5
		my_dict2['d']*)
(*ritorna 5*)

let usage_let = DictCall(Den("my_dict2"), "d");;
let second_let = Let("my_dict2", DictAdd(Den("my_dict"), ("d", Eint(5))), usage_let);;
let first_let = Let("my_dict", DictInit([("a", Eint(4));("b", Eint(1));("c", Eint(3))]), second_let);;

print (eval first_let env0) "my_dict['d']: ";;

(*Test3 -> Modifica elemento dizionario*)

(*let my_dict = {'a': 4, 'b': 1, 'c': 3} in
	let my_dict2 = my_dict['a'] = 7
		my_dict2['a']*)
(*ritorna 7*)

let usage_let = DictCall(Den("my_dict2"), "a");;
let second_let = Let("my_dict2", DictAdd(Den("my_dict"), ("a", Eint(7))), usage_let);;
let first_let = Let("my_dict", DictInit([("a", Eint(4));("b", Eint(1));("c", Eint(3))]), second_let);;

print (eval first_let env0) "my_dict['a']: ";;

(*Test4 -> Applyover su dizionario*)

(*let my_dict = {'a': 4, 'b': 1, 'c': 3} in
	let my_dict2 = ApplyOver ((fun x -> 5*x), "my_dict")
		my_dict2['a']*)
(*ritorna 20*)

let usage_let = DictCall(Den("my_dict2"), "a");;
let second_let = Let("my_dict2", DictApplyOver(Fun("x", Prod(Den("x"), Eint(5))), Den("my_dict")), usage_let);;
let first_let = Let("my_dict", DictInit([("a", Eint(4));("b", Eint(1));("c", Eint(3))]), second_let);;

print (eval first_let env0) "my_dict['a']: ";;

(*Test5 -> Applyover ricorsivo su dizionario*)

(*let my_dict = {'a': 4, 'b': 1, 'c': 3} in
	let rec fact x = if x = 1 then 1 else x*fact(x-1) in 
		let my_dict2 = ApplyOver (fact, "my_dict")
			my_dict2['a']*)
(*ritorna 24*)

let usage_let = DictCall(Den("my_dict2"), "a");;
let third_let = Let("my_dict2", DictApplyOver(Den("fact"), Den("my_dict")), usage_let);;
let fact = Fun("x", Ifthenelse (Eq(Den("x"), Eint(1)), Eint(1), Prod(Den("x"), FunCall(Den("fact"), Diff(Den("x"), Eint(1))))));;
let second_let = Letrec("fact", fact, third_let);;
let first_let = Let("my_dict", DictInit([("a", Eint(4));("b", Eint(1));("c", Eint(3))]), second_let);;

print (eval first_let env0) "my_dict['a']: ";;

(*Test6 -> Scoping dinamico*)

(*let x = 5 in
	let add n = x+n in
		let x = 4 in
			add 6*)
(*	ritorna 10 (DS)
	ritorna 11 (SS)*)

let usage_let = FunCall(Den("add"), Eint(6));;
let third_let = Let("x", Eint(4), usage_let);;
let second_let = Let("add", Fun("n", Sum(Den("x"), Den("n"))), third_let);;
let first_let = Let ("x", Eint(5), second_let);;

print (rt_eval first_let env0) "Scoping Statico: ";;
print (eval first_let env0) "Scoping Dinamico: ";;
