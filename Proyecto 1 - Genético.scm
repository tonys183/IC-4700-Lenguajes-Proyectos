#lang scheme

;==== FUNCIONES AUXILIARES ====;

;--- Altura ---;
; Dominio:
; Codominio:
(define altura
  (lambda (A)
    (cond
      ((null? A) -1)
      ((not (list? A)) 0)
      (else (+ 1 (max (altura (cadr A)) (altura (caddr A))))))))

;--- Nivel ---;
; Dominio:
; Codominio:
(define nivel
  (lambda (A n)
    (cond
      ((null? A) A)
      ((zero? n) (list A))
      ((not (list? A)) (list))
      ((list? A) (append (nivel (cadr A) (- n 1)) (nivel (caddr A) (- n 1))))
      (else (list)))))

;--- Reconstruir ---;
; Dominio:
; Codominio:
(define recons-inv-Arb
  (lambda (A)
    (cond
      ((or (null? A) (not (list? A))) A)
      (else (list (car A) (recons-inv-Arb (caddr A)) (recons-inv-Arb (cadr A)))))))

;--- Elemento en Índice ---;
; Dominio:
; Codominio:
(define elementoEn
  (lambda (L n)
    (cond
      ((zero? n) (car L))
      (else (elementoEn (cdr L) (- n 1))))))

;--- X ---;
; Dominio:
; Codominio:
(define pop
  (lambda (L)
    (cond
      ((or (null? (cdr L)) (null? L) ) '())
      (else (cons (car L) (pop (cdr L)) )))))

;--- X ---;
; Dominio:
; Codominio:
(define top
  (lambda (L)
    (cond
      ((null? (cdr L)) (car L))
      (else (top (cdr L) )))))

;--- X ---;
; Dominio:
; Codominio:
(define primeros-n
  (lambda (L n)
    (cond
      ((or (null? L) (zero? n)) '())
      (else (cons (car L) (primeros-n (cdr L) (- n 1)))))))

;--- Elemento en Índice ---;
; Dominio:
; Codominio:
(define quitarElemento
  (lambda (L n)
    (cond
      ((null? L) L)
      ((zero? n) (cdr L))
      (else (cons (car L) (quitarElemento (cdr L) (- n 1))) )
    )))

;--- X ---;
; Dominio:
; Codominio:
(define precisionK
  (lambda (n k)
   (/ (round (* n (expt 10 k))) (expt 10 k))))

;--- X ---;
; Dominio:
; Codominio:
(define realRand
  (lambda ()
    (cond
    ((< (random) 0.25) (precisionK (* -1 (* (random) (expt 10 (random 4)))) 6))
    (else (precisionK (* (random) (expt 10 (random 4))) 6)))))

;--- X ---;
; Dominio:
; Codominio:
(define realPositiveRand
  (lambda ()
    (precisionK (* (random) (expt 10 (random 4))) 6)))



;==== OPERACIONES ====;

;--- X ---;
; Dominio:
; Codominio:
(define divValida
  (lambda (a b)
    (cond
      ((or (eq? 'error a) (eq? 'error b)) 'error)
      ((= b 0) 'error)
      (else (/ a b)))))

;--- X ---;
; Dominio:
; Codominio:
(define expValida
  (lambda (b e)
    (cond
      ((or (eq? 'error e) (eq? 'error b)) 'error)
      ((and (zero? b) (< e 0)) 'error)
      ((and (< b 0) (not (integer? e)))
       (cond 
            ((complex? (- (expt (- b) e))) 'error)
            (else (- (expt (- b) e)))))
      ((> (abs e) 20) (expt b (random 10)))
      ((> (abs b) 1000) (expt (+ 1 (random 100)) e))
      (else (expt b e)))))

;--- X ---;
; Dominio:
; Codominio:
(define logValido
  (lambda (a b)
    (cond
      ((or (eq? 'error a) (eq? 'error b)) 'error)
      ((or (<= a 0) (<= b 1)) 'error)
      (else (divValida (log a) (log b))))))

;--- X ---;
; Dominio:
; Codominio:
(define sumaValida
  (lambda (a b)
      (cond
        ((or (eq? 'error a) (eq? 'error b)) 'error)
        ((or (eq? a -inf.0) (eq? b -inf.0) (eq? a +inf.0) (eq? b +inf.0) ) 'error)
        (else (+ a b) 
    ))))

;--- X ---;
; Dominio:
; Codominio:
(define restaValida
  (lambda (a b)
      (cond
        ((or (eq? 'error a) (eq? 'error b)) 'error)
        ((or (eq? a -inf.0) (eq? b -inf.0) (eq? a +inf.0) (eq? b +inf.0) ) 'error)
        (else (- a b) 
    ))))

;--- X ---;
; Dominio:
; Codominio:
(define mulValida
  (lambda (a b)
      (cond
        ((or (eq? 'error a) (eq? 'error b)) 'error)
        ((or (eq? a -inf.0) (eq? b -inf.0) (eq? a +inf.0) (eq? b +inf.0) ) 'error)
        (else (* a b) 
    ))))
  
;--- Definición de Operadores ---;
(define opsValidas (list
                    (cons '+ sumaValida)
                    (cons '- restaValida)
                    (cons '* mulValida)
                    (cons '/ divValida)
                    (cons 'loga_b logValido)
                    (cons '** expValida)))

;--- Operaciones Posibles ---;}
(define opsPosibles (map (lambda(i) (cdr i)) opsValidas)) ;Lista de funciones



;==== SET ====;

;--- X ---;
; Dominio:
; Codominio:
(define crearSet
  (lambda () (list)))

;--- X ---;
; Dominio:
; Codominio:
(define insertarSet
  (lambda (S x)
    (cond
      ((member x S equal?) S)
      (else (cons x S)))))



;==== MANEJO DE INDIVIDUOS ====;

;--- X ---;
; Dominio:
; Codominio:
(define evalOps
  (lambda (paramOp op)
    (cond
      ((member 'error paramOp equal?) 'error)
      ((member +inf.0 paramOp equal?) 'error)
      ((member -inf.0 paramOp equal?) 'error)
      (else (apply op paramOp)))))

;--- X ---;
; Dominio:
; Codominio:
(define evalArbol
  (lambda (arbol x y)
    (cond
      ((number? arbol) arbol)
      ((symbol? arbol)
       (cond
         ((eq? arbol 'x) x)
         (else y)))
      (else (evalOps (map (lambda (subArb) (evalArbol subArb x y)) (cdr arbol)) (car arbol))))))



;==== PROCESO GENÉTICO ====;

;--- X ---;
; Dominio:
; Codominio:
(define cercaniaXYZ
  (lambda (A)
    (cond
      ((null? A) 'error)
      (else
      (foldl sumaValida
            0
            (map (lambda (p) (expValida (restaValida (caddr p) (evalArbol A (car p) (cadr p))) 2)) pts))))))

;--- X ---;
; Dominio:
; Codominio:
(define totalGen
  (lambda (difs) ;Lista de cercaniaXYZ de todos los individuos
    (apply + (map (lambda (d) (cond
                                  ((equal? d 'error) (/ 1 1000))
                                  (else (/ 1 (+ 1 d))))) difs))))

;--- X ---;
; Dominio:
; Codominio:
(define fitness
  (lambda (difs total)
    (map (lambda (d) (cond
                      ((equal? d 'error) (/ (/ 1 1000) total))
                      (else (/ (/ 1 (+ 1 d)) total)))) difs)))

;--- X ---;
; Dominio:
; Codominio:
(define escogerElite
  (lambda (S F max)
    (cond
      ((or (null? S) (null? F)) S)
      ((equal? (car F) max) (car S))
      (else (escogerElite (cdr S) (cdr F) max)))))



;==== AUXILIARES MUTACIÓN ====;

;--- Número random de las hojas ---;
; Dominio:
; Codominio:
(define elegirNumRand
  (lambda (A)
    (cond
      ((null? (filter number? (nivel A (altura A)))) 0)
      (else  (elementoEn (filter number? (nivel A (altura A))) 
          (random (length (filter number? (nivel A (altura A))) ) )))
      )))
   

;--- X ---;
; Dominio:
; Codominio:
(define elegirHoja
  (lambda (A ruta)
    (cond
      ((or (null? A) (not (list? A))) (cons A ruta))
      ((< (random) 0.5 ) (cons 0 (elegirHoja (cadr A) ruta)))
      (else (cons 1 (elegirHoja (caddr A) ruta))))))

;--- Meter un valor al árbol ---;
; Dominio:
; Codominio:
(define cambiarElemento
  (lambda (A R e)
  (cond
    ((null? R) e)
    ((= 0 (car R)) (list (car A) (cambiarElemento (cadr A) (cdr R) e ) (caddr A)))
    (else (list (car A) (cadr A) (cambiarElemento (caddr A) (cdr R) e))))))

;--- X ---;
; Dominio:
; Codominio:
(define swapHojas
  (lambda (A R1 R2)
    (cambiarElemento (cambiarElemento A (pop R2) (top R1)) (pop R1) (top R2))))

;--- Cambiar Operador ---;
; Dominio:
; Codominio:
(define cambiarOp
  (lambda (A nvl op)
    (cond
      ((or (not (list? A)) (null? A)) A)
      ((zero? nvl) (list op (cadr A) (caddr A)))
      ((and (not (list? (cadr A))) (not (list? (caddr A))) 
            (not (member (cadr A) opsPosibles equal?)) (not (member (caddr A) opsPosibles equal?)))
        (list op (cadr A) (caddr A)))
      ((<= (random) 0.5) (list (car A) (cambiarOp (cadr A) (- nvl 1) op) (caddr A)))
      (else (list (car A) (cadr A) (cambiarOp (caddr A) (- nvl 1) op))))))

;--- X ---;
; Dominio:
; Codominio:
(define cambiarNum
  (lambda (A nvl nuevoNum hoja)
    (cond 
      ((or (not (list? A)) (null? A)) A)
      ((zero? nvl) (cond ((equal? A hoja) nuevoNum) 
                          (else A)))
      ((list? A) (list (car A) (cambiarNum (cadr A) (- nvl 1) nuevoNum hoja) 
                                (cambiarNum (caddr A) (- nvl 1) nuevoNum hoja)))
      (else A))))

;--- X ---;
; Dominio:
; Codominio:
(define quitar-rama-aux
  (lambda (A ruta)
    (cond
      ((or (not (list? A)) (null? A)) A)
      ((null? ruta) (arbol-altura-n 1))
      ((= 0 (car ruta)) (list (car A) (quitar-rama-aux (cadr A) (cdr ruta)) (caddr A)))
      (else (list (car A) (cadr A) (quitar-rama-aux (caddr A) (cdr ruta)))))))

;--- X ---;
; Dominio:
; Codominio:
(define quitar-rama
  (lambda (A ruta)
    (quitar-rama-aux A (primeros-n ruta (+ 1 (random (- (length ruta) 2)))))))

;--- X ---;
; Dominio:
; Codominio:
(define pegar-subArbol
  (lambda (A ruta)
    (cond 
      ((null? ruta) (arbol-aleatorio (+ 1 (random 3)) (+ 1 (random 2))))
      ((zero? (car ruta)) (list (car A) (pegar-subArbol (cadr A) (cdr ruta)) (caddr A)))
      (else (list (car A) (cadr A) (pegar-subArbol (caddr A) (cdr ruta)))))))



;==== MUTACIONES Y CRUCES ====;

;--- Supermutar ---;
; Dominio:
; Codominio:
(define supermutar
  (lambda (A p)
    (cond
      ((< p 0.25) (quitar-rama A (elegirHoja A '()))) ; Agarra una rama y la remplaza por un valor 'x,'y o n / n <- R
      ((< p 0.5) (recons-inv-Arb A)) ; Cambia cada valor, y cada rama derecha por su izquierda, y viceveresa.
      (else (pegar-subArbol A (pop (elegirHoja A '()))))))) ; Añade un arbol aletorio al arbol, remplaza una hoja para hacerlo.

;--- Mutar ---;
; Dominio:
; Codominio:
(define mutar
  (lambda (A p)
    (cond
      ((< (altura A) 1) A)
      ((< p 0.25) (cambiarOp A (random (altura A)) (elementoEn opsPosibles (random (length opsPosibles))) ))
      ((< p 0.50) (cambiarNum A (altura A) (realRand) (elegirNumRand A)))
      ((< p 0.75) (swapHojas A (elegirHoja A '()) (elegirHoja A '())))
      (else (cambiarOp (cambiarNum A (altura A) (realRand) (elegirNumRand A)) 
                       (random (altura A)) 
                       (elementoEn opsPosibles (random (length opsPosibles))))))))

;--- Cruce ---;
; Dominio:
; Codominio:
(define cruces
  (lambda (A B)
    (cond
      ((null? B) A)
      ((null? A) B)
      ((< (random) 0.01) (supermutar (list (car A) (cadr A) (caddr B)) (random)))
      ((< (random) 0.05) (mutar (list (car A) (cadr A) (caddr B)) (random))) 
      (else (list (car A) (cadr A) (caddr B))))))

    
;--- X ---;
; Dominio:
; Codominio:
(define selectIntervalo
  (lambda (listaFits rango)
    (cond
      ((null? listaFits) (list))
      (else (cons (+ (car listaFits) rango) (selectIntervalo (cdr listaFits) (+ rango (car listaFits))))))))

;--- X ---;
; Dominio:
; Codominio:
(define selectIndiv
  (lambda (genActual listaInterv rand)
   (cond
    ((null? genActual) genActual)
    ((< rand (car listaInterv)) (car genActual))
    (else (selectIndiv (cdr genActual) (cdr listaInterv) rand))))
  )



;==== GENERACIÓN DE ÁRBOLES ====;

;--- X ---;
; Dominio:
; Codominio:
(define hoja-aleatoria
  (lambda (p)
    (cond
        ((< p 0.33334) (realPositiveRand) ) ; Solo positivos para evitar las indefiniciones por completo.
        ((< p 0.66667) 'x)
        (else 'y))))

;--- X ---;
; Dominio:
; Codominio:
(define arbol-altura-n 
  (lambda (n)
    (cond
      ((zero? n) (hoja-aleatoria (random)))
      (else (list (elementoEn opsPosibles (random (length opsPosibles))) (arbol-altura-n (- n 1)) (arbol-altura-n (- n 1)))))))

;--- X ---;
; Dominio:
; Codominio:
(define arbol-aleatorio
  (lambda (n m) ; n es la cantidad de subarboles que quiero, m es la altura maxima de cada subarbol m<-N ^ m<-[1,+oo[. (m!=0).
    (cond
      ((= n 1) (arbol-altura-n (+ 1 (random m))))
      (else (list (elementoEn opsPosibles (random (length opsPosibles))) (arbol-aleatorio (- n 1) m) (arbol-aleatorio (- n 1) m)))))) 



;==== MANEJO DE POBLACIONES ====;

;--- X ---;
; Dominio:
; Codominio:
(define generarPoblacion
  (lambda (n)
    (cond
      ((zero? n) (crearSet))
      (else (cons (arbol-aleatorio 2 2) (generarPoblacion (- n 1)))))))

;--- X ---;
; Dominio:
; Codominio:
(define mejorGen
  (lambda (genActual fits)
  ;(print "Cerc") (print (map (lambda (ind) (cercaniaXYZ ind)) genActual)) (newline) (newline)
    ;(print (fitness (map (lambda (ind) (cercaniaXYZ ind)) genActual) (totalGen (map (lambda (ind) (cercaniaXYZ ind)) genActual)))) (newline) (newline)
    (list (escogerElite genActual fits (apply max fits)) )))

;--- X ---;
; Dominio:
; Codominio:
(define generar-descendencia
  (lambda (DES P n fits)  ; Descendencia, padres, numero de hijos.
  (cond
    ((zero? n) DES)
    (else (cons 
      (cruces (selectIndiv P (selectIntervalo fits 0) (random)) (selectIndiv P (selectIntervalo fits 0) (random))) 
      (generar-descendencia DES P (- n 1) fits)) )
  )))

;--- X ---;
; Dominio:
; Codominio:
(define algoritmo-genetico
  (lambda(PG n)
  (cond
  ((zero? n) (car (mejorGen PG (fitness (map (lambda (ind) (cercaniaXYZ ind)) PG) (totalGen (map (lambda (ind) (cercaniaXYZ ind)) PG))))))
  (else (algoritmo-genetico (generar-descendencia (mejorGen PG (fitness (map (lambda (ind) (cercaniaXYZ ind)) PG) (totalGen (map (lambda (ind) (cercaniaXYZ ind)) PG)))) PG (length PG)
  (fitness (map (lambda (ind) (cercaniaXYZ ind)) PG) (totalGen (map (lambda (ind) (cercaniaXYZ ind)) PG)))) 
  (- n 1)))
  )))

(define pts
  '((7 1 8)
    (18 13 31)
    (9 9 18)
    (14 4 18)
    (10 9 19)
    (18 1 19)
    (16 5 21)
    (0 16 16)
    (19 11 30)
    (10 9 19)
    (14 4 18)
    (12 18 30)
    (3 11 14)
    (10 0 10)
    (17 17 34)
    (2 0 2)
    (17 15 32)
    (15 1 16)
    (2 18 20)
    (4 1 5)
    (13 19 32)
    (5 19 24)
    (14 8 22)
    (9 18 27)
    (16 12 28)
    (3 12 15)
    (15 12 27)
    (0 15 15)
    (13 18 31)
    (9 19 28)
    (7 4 11)
    (19 19 38)
    (14 19 33)
    (6 6 12)
    (8 17 25)
    (9 3 12)
    (14 2 16)
    (3 1 4)
    (13 5 18)
    (12 6 18)))

(define gen (generarPoblacion 100))
(algoritmo-genetico gen 100)