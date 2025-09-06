#lang racket
(require plot racket/gui/base)
(plot-new-window? #f)

;==== FUNCIONES AUXILIARES ====;

;--- Altura ---;
; Dominio: Un árbol.
; Codominio: Un entero (altura del árbol).
(define altura
  (lambda (A)
    (cond
      ((null? A) -1)
      ((not (list? A)) 0)
      (else (+ 1 (max (altura (cadr A)) (altura (caddr A))))))))


;--- Nivel ---;
; Dominio: Un árbol y un entero n (nivel).
; Codominio: Un conjunto de nodos.
(define nivel
  (lambda (A n)
    (cond
      ((null? A) A)
      ((zero? n) (list A))
      ((not (list? A)) (list))
      ((list? A) (append (nivel (cadr A) (- n 1)) (nivel (caddr A) (- n 1))))
      (else (list)))))



;--- Elemento en Índice ---;
; Dominio: Una lista y un entero n.
; Codominio: Un valor de la lista.
(define elementoEn
  (lambda (L n)
    (cond
      ((zero? n) (car L))
      (else (elementoEn (cdr L) (- n 1))))))

;--- Pop ---;
; Dominio: Una lista.
; Codominio: Una lista sin el último elemento.
(define pop
  (lambda (L)
    (cond
      ((or (null? (cdr L)) (null? L) ) '())
      (else (cons (car L) (pop (cdr L)) )))))

;--- Top ---;
; Dominio: Una lista.
; Codominio: El último valor de la lista.
(define top
  (lambda (L)
    (cond
      ((null? (cdr L)) (car L))
      (else (top (cdr L) )))))

;--- Primeros-n ---;
; Dominio: Una lista y un entero n.
; Codominio: Una lista con los n primeros elementos.
(define primeros-n
  (lambda (L n)
    (cond
      ((or (null? L) (zero? n)) '())
      (else (cons (car L) (primeros-n (cdr L) (- n 1)))))))

;--- Quitar Elemento ---;
; Dominio: Una lista y un entero n.
; Codominio: Una lista sin el enésimo valor.
(define quitarElemento
  (lambda (L n)
    (cond
      ((null? L) L)
      ((zero? n) (cdr L))
      (else (cons (car L) (quitarElemento (cdr L) (- n 1))) )
    )))

;--- Presición k ---;
; Dominio: Dos enteros, n y k.
; Codominio: Un valor real con k o menos decimales y parte entera n.
(define precisionK
  (lambda (n k)
   (/ (round (* n (expt 10 k))) (expt 10 k))))

;--- Real Random ---;
; Dominio: Vacio.
; Codominio: Un valor real.
(define realRand
  (lambda ()
    (cond
    ((< (random) 0.5) (precisionK (* -1 (* (random) (expt 10 (random 2)))) 6))
    (else (precisionK (* (random) (expt 10 (random 2))) 6)))))

;--- Real Random No negativo ---;
; Dominio: Vacio.
; Codominio: Un valor real no negativo.
(define realPositiveRand
  (lambda ()
    (precisionK (* (random) (expt 10 (random 2))) 6)))



;==== OPERACIONES ====;

;--- divValida ---;
; Dominio: Dos números reales a y b.
; Codominio: El cociente a/b o 'error si la división no es válida.
(define divValida
  (lambda (a b)
    (cond
      ((or (eq? 'error a) (eq? 'error b)) 'error)
      ((= b 0) 'error)
      (else (/ a b)))))

;--- expValida ---;
; Dominio: Dos números reales b (base) y e (exponente).
; Codominio: El resultado de b^e o 'error si la operación no es válida.
(define expValida
  (lambda (b e)
    (cond
      ((or (eq? 'error e) (eq? 'error b)) 'error)
      ((and (zero? b) (< e 0)) 'error)
      ((and (< b 0) (not (integer? e))) (expt (abs b) e))
      ((> (abs e) 20) (expt b (random 10)))
      ((> (abs b) 1000) (expt (+ 1 (random 100)) e))
      (else (expt b e)))))

;--- logValido ---;
; Dominio: Dos números reales a y b.
; Codominio: El logaritmo de a en base b o 'error si no es válido.
(define logValido
  (lambda (a b)
    (cond
      ((or (eq? 'error a) (eq? 'error b)) 'error)
      ((or (<= a 0) (<= b 0) (= b 1)) 'error)
      (else (divValida (log a) (log b))))))

;--- sumaValida ---;
; Dominio: Dos números reales a y b.
; Codominio: La suma a+b o 'error si no es válida.
(define sumaValida
  (lambda (a b)
      (cond
        ((or (eq? 'error a) (eq? 'error b)) 'error)
        ((or (eq? a -inf.0) (eq? b -inf.0) (eq? a +inf.0) (eq? b +inf.0) ) 'error)
        (else (+ a b) 
    ))))

;--- restaValida ---;
; Dominio: Dos números reales a y b.
; Codominio: La resta a-b o 'error si no es válida.
(define restaValida
  (lambda (a b)
      (cond
        ((or (eq? 'error a) (eq? 'error b)) 'error)
        ((or (eq? a -inf.0) (eq? b -inf.0) (eq? a +inf.0) (eq? b +inf.0) ) 'error)
        (else (- a b) 
    ))))

;--- mulValida ---;
; Dominio: Dos números reales a y b.
; Codominio: El producto a*b o 'error si no es válido.
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

;--- Operaciones Posibles ---;
(define opsPosibles (map (lambda(i) (cdr i)) opsValidas)) ;Lista de funciones



;==== SET ====;

;--- crearSet ---;
; Dominio: Vacio.
; Codominio: Un conjunto vacío (representado como lista).
(define crearSet
  (lambda () (list)))

;--- insertarSet ---;
; Dominio: Un conjunto S y un elemento x.
; Codominio: El conjunto S con x insertado (sin duplicados).
(define insertarSet
  (lambda (S x)
    (cond
      ((member x S equal?) S)
      (else (cons x S)))))



;==== MANEJO DE INDIVIDUOS ====;

;--- evalOps-helper ---;
; Dominio: Un valor resultante de una operación.
; Codominio: Un número real o COTA_ERROR.
(define evalOps-helper
  (lambda (result)
    (cond
      ((eq? result 'error) COTA_ERROR)
      ((equal? result +inf.0) COTA_ERROR)
      ((equal? result -inf.0) COTA_ERROR)
      (else result))))

    
;--- evalOps ---;
; Dominio: Una lista de parámetros paramOp y una operación op.
; Codominio: El resultado de aplicar op a paramOp o COTA_ERROR.
(define evalOps
  (lambda (paramOp op)
    (cond
      ((member 'error paramOp equal?) COTA_ERROR)
      ((member +inf.0 paramOp equal?) COTA_ERROR)
      ((member -inf.0 paramOp equal?) COTA_ERROR)
      (else (evalOps-helper (apply op paramOp))))))

;--- evalArbol ---;
; Dominio: Un árbol de operaciones y dos números reales x e y.
; Codominio: Un número real resultante de evaluar el árbol.
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

;--- cercaniaXYZ ---;
; Dominio: Un árbol de operaciones A.
; Codominio: Un número real que representa la cercanía o COTA_ERROR.
(define cercaniaXYZ
  (lambda (A)
    (cond
      ((null? A) COTA_ERROR)
      (else
       (foldl
        sumaValida
        0
        (map (lambda (p) (cond ((eq? (evalArbol A (car p) (cadr p)) 'error) COTA_ERROR)
                 (else (expValida (/ (restaValida (caddr p) (evalArbol A (car p) (cadr p))) (+ 1 (abs (caddr p)))) 2))))
             pts))))))

;--- totalGen ---;
; Dominio: Una lista difs con los valores de cercaniaXYZ de los individuos.
; Codominio: Un número real que representa el total acumulado.
(define totalGen
  (lambda (difs) 
    (apply + (map (lambda (d) (cond
                                  ((equal? d 'error) (/ 1 1000))
                                  (else (/ 1 (+ 1 d))))) difs))))

;--- fitness ---;
; Dominio: Una lista difs de valores de cercanía y un número real total.
; Codominio: Una lista de números reales que representan los valores de fitness.
(define fitness
  (lambda (difs total)
    (map (lambda (d) (cond
                      ((equal? d 'error) (/ (/ 1 1000) total))
                      (else (/ (/ 1 (+ 1 d)) total)))) difs)))

;--- escogerElite ---;
; Dominio: Una lista de individuos S, una lista de valores F y un número real max.
; Codominio: Un individuo de la lista S.
(define escogerElite
  (lambda (S F max)
    (cond
      ((or (null? S) (null? F)) S)
      ((equal? (car F) max) (car S))
      (else (escogerElite (cdr S) (cdr F) max)))))



;==== AUXILIARES MUTACIÓN ====;

;--- elegirNumRand ---;
; Dominio: Un árbol A.
; Codominio: Un número real (hoja del árbol) o 0 si no hay hojas numéricas.
(define elegirNumRand
  (lambda (A)
    (cond
      ((null? (filter number? (nivel A (altura A)))) 0)
      (else (elementoEn (filter number? (nivel A (altura A))) 
          (random (length (filter number? (nivel A (altura A)))))))
      )))
   
;--- elegirHoja ---;
; Dominio: Un árbol A y una ruta.
; Codominio: Una lista que representa la ruta hacia una hoja.
(define elegirHoja
  (lambda (A ruta)
    (cond
      ((or (null? A) (not (list? A))) (cons A ruta))
      ((< (random) 0.5 ) (cons 0 (elegirHoja (cadr A) ruta)))
      (else (cons 1 (elegirHoja (caddr A) ruta))))))

;--- cambiarElemento ---;
; Dominio: Un árbol A, una ruta R y un elemento e.
; Codominio: El árbol A con el elemento en la ruta R reemplazado por e.
(define cambiarElemento
  (lambda (A R e)
  (cond
    ((null? R) e)
    ((= 0 (car R)) (list (car A) (cambiarElemento (cadr A) (cdr R) e ) (caddr A)))
    (else (list (car A) (cadr A) (cambiarElemento (caddr A) (cdr R) e))))))

;--- swapHojas ---;
; Dominio: Un árbol A y dos rutas R1 y R2.
; Codominio: El árbol A con las hojas en R1 y R2 intercambiadas.
(define swapHojas
  (lambda (A R1 R2)
    (cambiarElemento (cambiarElemento A (pop R2) (top R1)) (pop R1) (top R2))))

;--- cambiarOp ---;
; Dominio: Un árbol A, un entero nvl y un operador op.
; Codominio: El árbol A con el operador cambiado en el nivel dado.
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

;--- cambiarNum ---;
; Dominio: Un árbol A, un entero nvl, un número nuevoNum y una hoja.
; Codominio: El árbol A con la hoja reemplazada por nuevoNum en el nivel dado.
(define cambiarNum
  (lambda (A nvl nuevoNum hoja)
    (cond 
      ((or (not (list? A)) (null? A)) A)
      ((zero? nvl) (cond ((equal? A hoja) nuevoNum) 
                          (else A)))
      ((list? A) (list (car A) (cambiarNum (cadr A) (- nvl 1) nuevoNum hoja) 
                                (cambiarNum (caddr A) (- nvl 1) nuevoNum hoja)))
      (else A))))

;--- quitar-rama-aux ---;
; Dominio: Un árbol A y una ruta.
; Codominio: El árbol A con la subrama eliminada en la ruta indicada.
(define quitar-rama-aux
  (lambda (A ruta)
    (cond
      ((or (not (list? A)) (null? A)) A)
      ((null? ruta) (arbol-altura-n 1))
      ((= 0 (car ruta)) (list (car A) (quitar-rama-aux (cadr A) (cdr ruta)) (caddr A)))
      (else (list (car A) (cadr A) (quitar-rama-aux (caddr A) (cdr ruta)))))))

;--- quitar-rama ---;
; Dominio: Un árbol A y una ruta.
; Codominio: El árbol A con una rama eliminada en una posición aleatoria de la ruta.
(define quitar-rama
  (lambda (A ruta)
    (quitar-rama-aux A (primeros-n ruta (+ 1 (random (- (length ruta) 2)))))))

;--- pegar-subArbol ---;
; Dominio: Un árbol A y una ruta.
; Codominio: El árbol A con un subárbol aleatorio pegado en la ruta indicada.
(define pegar-subArbol
  (lambda (A ruta)
    (cond 
      ((null? ruta) (arbol-aleatorio (+ 1 (random 1)) (+ 1 (random 1))))
      ((zero? (car ruta)) (list (car A) (pegar-subArbol (cadr A) (cdr ruta)) (caddr A)))
      (else (list (car A) (cadr A) (pegar-subArbol (caddr A) (cdr ruta)))))))

    
;--- cambiar-hojas-variables ---;
; Dominio: Un árbol A.
; Codominio: El árbol A con las hojas reemplazadas aleatoriamente por 'x o 'y.
(define cambiar-hojas-variables
  (lambda (A)
    (cond
      ((or (not (list? A)) (null? A)) (cond ((< (random) 0.5) 'x) (else 'y)))
      (else (list (car A) (cambiar-hojas-variables (cadr A)) (cambiar-hojas-variables (caddr A))))
    )))



;==== MUTACIONES Y CRUCES ====;

;--- supermutar ---;
; Dominio: Un árbol A y un número real p.
; Codominio: Un árbol A con modificaciones aleatorias.
(define supermutar
  (lambda (A p)
    (cond
      ((< p 0.5) (quitar-rama A (elegirHoja A '()))) ; Agarra una rama y la remplaza por un valor 'x,'y o n / n <- R
      ((< p 0.75) (cambiar-hojas-variables A)) 
      (else (pegar-subArbol A (pop (elegirHoja A '()))))))) ; Añade un arbol aletorio al arbol, remplaza una hoja para hacerlo.

;--- mutar ---;
; Dominio: Un árbol A y un número real p.
; Codominio: Un árbol A mutado aleatoriamente.
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

;--- cruces-aux ---;
; Dominio: Un árbol cruce y un valor-cruce numérico.
; Codominio: Un árbol cruce posiblemente mutado o supermutado.
(define cruces-aux
  (lambda (cruce valor-cruce)
    (cond
      ((< (random) 0.05) (supermutar cruce (random)))
      ((< (random) 0.1) (mutar cruce (random)))
      (else cruce)
    )))

;--- cruces ---;
; Dominio: Dos árboles A y B.
; Codominio: Un nuevo árbol resultado del cruce de A y B.
(define cruces
  (lambda(A B)
  (cond
    ((null? B) A)
    ((null? A) B)
    (else (cruces-aux (list (car A) (cadr A) (caddr B)) 
      (cercaniaXYZ (list (car A) (cadr A) (caddr B))) ))
  )))

;--- selectIntervalo ---;
; Dominio: Una lista de valores de fitness y un número real rango.
; Codominio: Una lista de números reales que representan intervalos acumulados.
(define selectIntervalo
  (lambda (listaFits rango)
    (cond
      ((null? listaFits) (list))
      (else (cons (+ (car listaFits) rango) (selectIntervalo (cdr listaFits) (+ rango (car listaFits))))))))


;--- selectIndiv ---;
; Dominio: Una lista genActual, una lista de intervalos listaInterv y un número real rand.
; Codominio: Un árbol seleccionado de genActual.
(define selectIndiv
  (lambda (genActual listaInterv rand)
   (cond
    ((null? genActual) genActual)
    ((< rand (car listaInterv)) (car genActual))
    (else (selectIndiv (cdr genActual) (cdr listaInterv) rand))))
  )



;==== GENERACIÓN DE ÁRBOLES ====;

;--- hoja-aleatoria ---;
; Dominio: Un número real p.
; Codominio: Un número real positivo o el símbolo 'x o 'y.
(define hoja-aleatoria
  (lambda (p)
    (cond
        ((< p 0.33334) (realPositiveRand) )
        ((< p 0.66667) 'x)
        (else 'y))))

;--- arbol-altura-n ---;
; Dominio: Un número entero n (altura del árbol).
; Codominio: Un árbol de altura n con hojas aleatorias.
(define arbol-altura-n 
  (lambda (n)
    (cond
      ((zero? n) (hoja-aleatoria (realRand)))
      (else (list (elementoEn opsPosibles (random (length opsPosibles))) (arbol-altura-n (- n 1)) (arbol-altura-n (- n 1)))))))

;--- arbol-aleatorio ---;
; Dominio: Dos números enteros n (cantidad de subárboles) y m (altura máxima).
; Codominio: Un árbol aleatorio con n subárboles de altura ≤ m.
(define arbol-aleatorio
  (lambda (n m)
    (cond
      ((= n 1) (arbol-altura-n (+ 2 (random 3))))
      (else (list (elementoEn opsPosibles (random (length opsPosibles))) (arbol-aleatorio (- n 1) m) (arbol-aleatorio (- n 1) m)))))) 



;==== MANEJO DE POBLACIONES ====;

;--- generarPoblacion ---;
; Dominio: Un número entero n.
; Codominio: Una lista de árboles aleatorios de tamaño n.
(define generarPoblacion
  (lambda (n)
    (cond
      ((zero? n) (crearSet))
      (else (cons (arbol-aleatorio (+ 1 (random 2)) (+ 1 (random 2))) (generarPoblacion (- n 1)))))))

;--- mejorGen ---;
; Dominio: Una lista genActual de árboles y una lista de valores de fitness fits.
; Codominio: Una lista con el árbol más apto (elite) de genActual.
(define mejorGen
  (lambda (genActual fits)
    ;(print "Cerc") (print (map (lambda (ind) (cercaniaXYZ ind)) genActual)) (newline) (newline)
    ;(print "ELITE: ") (print (escogerElite genActual fits (apply max fits))) (newline) (newline)
    ;(print (fitness (map (lambda (ind) (cercaniaXYZ ind)) genActual) (totalGen (map (lambda (ind) (cercaniaXYZ ind)) genActual)))) (newline) (newline)
    (list (escogerElite genActual fits (apply max fits)) )))

;--- X ---;
; Dominio: lista de árboles DES, lista de árboles P, número entero n,
;          lista de números reales entre 0 y 1 inclusivo fits y un árbol cruce.
; Codominio: lista de árboles.
(define generar-descendencia-aux
  (lambda(DES P n fits cruce)
  (cond
  ((zero? n) DES)
  ((member cruce DES equal?) (generar-descendencia-aux DES P n fits (cruces (selectIndiv P (selectIntervalo fits 0) (random)) (selectIndiv P (selectIntervalo fits 0) (random)))))
  (else (cons cruce (generar-descendencia-aux DES P (- n 1) fits (cruces (selectIndiv P (selectIntervalo fits 0) (random)) (selectIndiv P (selectIntervalo fits 0) (random))) )))
  )))

;--- X ---;
; Dominio: lista de árboles DES, lista de árboles P, número entero n y
;          lista de números reales entre 0 y 1 inclusivo fits.
; Codominio: lista de árboles.
(define generar-descendencia
  (lambda (DES P n fits)  ; Descendencia, padres, numero de hijos.
    (generar-descendencia-aux DES P n fits 
      (cruces (selectIndiv P (selectIntervalo fits 0) (random)) (selectIndiv P (selectIntervalo fits 0) (random))))
  ))

;--- X ---;
; Dominio: Lista de árboles de alguna generación.
; Codominio: Lista de números reales entre 0 y 1 inclusivo.
(define get-fitness
  (lambda (PG)
      (fitness (map (lambda (ind) (cercaniaXYZ ind)) PG)
          (totalGen (map (lambda (ind) (cercaniaXYZ ind)) PG)))
  ))

;--- X ---;
; Dominio: Lista de árboles de una generación, número entero n.
; Codominio: Un árbol.
(define algoritmo-genetico
  (lambda(PG n)
  (cond
  ;((zero? n) (print "fin-gen") (newline) (car (mejorGen PG (get-fitness PG))))
  ((>= 0.05 (cercaniaXYZ (car (mejorGen PG (get-fitness PG) )))) 
    (car (mejorGen PG (get-fitness PG))))
  (else (algoritmo-genetico (generar-descendencia (mejorGen PG (get-fitness PG) ) PG (- (length PG) 1) (get-fitness PG)) 
  (- n 1)))
  )))



;==== DEFINICIÓN DE DATOS ====;
(define COTA_ERROR 1.0e7)

(define pts1
  '((19 15 29346.35851851852)
    (2 9 1.0534979423868314)
    (2 2 21.333333333333332)
    (6 14 105.79591836734694)
    (7 10 448.18666666666667)
    (7 18 138.32921810699588)
    (6 5 829.44)
    (2 13 0.504930966469428)
    (2 4 5.333333333333333)
    (6 14 105.79591836734694)))


(define pts2
  '((4 17 -2.4497720911755554)
    (18 18 89295665425899.97)
    (14 15 -5886448.888255767)
    (5 1 0.15592448293623584)
    (6 0 0.09047539154589039)
    (2 11 -0.011618783380305042)
    (3 12 -0.12606209562358486)
    (15 16 -38931442.58443023)
    (9 12 -1318.6359677907499)
    (12 14 -181011.55860384842)
    (4 15 -1.5999968706280614)
    (7 3 0.7676317512752314)
    (13 4 14.10577688348565)
    (7 4 1.7429185241417005)
    (9 10 -972.9858294278807)
    (17 3 25.555036074522636)
    (2 8 -0.022675969371921127)
    (7 18 -1355.8618354315115)
    (6 17 -150.36511151795014)
    (2 3 -0.29251189747407047)
    (17 19 -2711501213.613263)
    (19 12 2381741.3955791583)
    (9 0 0.1219744326565336)
    (7 0 0.09461557702483518)
    (2 2 2.718281828459045)
    (8 14 -847.1487092209862)
    (5 14 -8.697010469045177)
    (9 2 0.5807556858098182)
    (2 13 -0.008237769270189607)
    (11 2 1.0412422649541293)
    (6 10 -12.716588105740703)
    (17 12 751887.6135417097)
    (8 0 0.10499644237143123)
    (12 18 -1882257.8027239882)
    (2 2 2.718281828459045)
    (17 6 701.4163925167654)
    (3 14 -0.1365009957082285)
    (12 5 24.875518175753292)
    (2 11 -0.011618783380305042)
    (19 5 581.8154927034864)))


(define pts4
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


(define pts5
  '((6 10 -64)
    (6 9 -45)
    (19 9 280)
    (18 19 -37)
    (6 5 11)
    (7 18 -275)
    (2 4 -12)
    (7 16 -207)
    (1 5 -24)
    (4 18 -308)
    (0 13 -169)
    (1 8 -63)
    (11 14 -75)
    (7 17 -240)
    (0 19 -361)
    (2 0 4)
    (15 9 144)
    (13 0 169)
    (12 14 -52)
    (10 11 -21)
    (3 6 -27)
    (1 11 -120)
    (10 7 51)
    (0 9 -81)
    (14 19 -165)
    (9 15 -144)
    (4 10 -84)
    (3 8 -55)
    (9 1 80)
    (16 19 -105)
    (2 4 -12)
    (17 11 168)
    (14 19 -165)
    (19 11 240)
    (7 2 45)
    (5 14 -171)
    (6 14 -160)
    (4 2 12)
    (1 7 -48)
    (2 3 -5)))


(define pts
  '((11 3 0.6274735630753033)
    (11 2 0.5404763088546395)
    (17 11 0.8711164930263436)
    (19 18 0.983974444213354)
    (10 17 1.184929413512433)
    (3 6 1.2920296742201791)
    (8 13 1.1760912590556811)
    (7 13 1.2324867603589635)
    (13 15 1.0462189153299533)
    (3 11 1.5936926411670824)
    (6 7 1.0566416671474377)
    (4 5 1.0860331325016919)
    (1 2 1.2618595071429148)
    (11 19 1.1869717539906937)
    (16 19 1.0533324751067528)
    (18 16 0.9648297958439285)
    (4 9 1.3382908331057726)
    (19 12 0.8668214419824227)
    (4 18 1.6719500161730103)
    (14 5 0.701838730514401)
    (3 12 1.6397385131955606)
    (4 5 1.0860331325016919)
    (5 10 1.276989408269624)
    (0 17 4.247927513443585)
    (7 13 1.2324867603589635)
    (12 9 0.9086181061280522)
    (13 5 0.7185650207899779)
    (19 12 0.8668214419824227)
    (10 5 0.783091851446946)
    (4 15 1.5812464746045694)
    (2 0 0.5)
    (11 19 1.1869717539906937)
    (6 12 1.2691183073525347)
    (1 5 1.7712437491614221)
    (7 13 1.2324867603589635)
    (4 7 1.226294385530917)
    (8 17 1.2787536009528289)
    (14 1 0.3962406251802891)
    (14 9 0.8648579046593244)
    (3 17 1.8294828004351504)))


;==== INTERFAZ PLOT 3D ====

; Convierte un árbol en una función de Racket f(x,y) -> z
(define convertir-arbol-a-funcion
  (lambda (arbol)
    (lambda (x y) (evalArbol arbol x y))))

; Convierte '(x y z) -> #(x y z) para points3d
(define pts->vectors
  (lambda (pts)
    (map (lambda (p) (vector (car p) (cadr p) (caddr p))) pts)))

; Obtiene rangos [xmin xmax] y [ymin ymax] a partir de los puntos
(define rangos-xy
  (lambda (pts)
    (values (apply min (map car pts))
            (apply max (map car pts))
            (apply min (map cadr pts))
            (apply max (map cadr pts)))))

; Ventana persistente para graficar y reutilizar la misma pestaña
(define plot-frame #f)
(define plot-canvas #t)
(define last-renderer3d #t)

(define plot-canvas%
  (class canvas%
    (super-new)
    (define/override (on-paint)
      (when last-renderer3d
        (define dc (send this get-dc))
        (define w (send this get-width))
        (define h (send this get-height))
        (plot3d/dc last-renderer3d dc 0 0 w h)))))

(define ensure-plot-window
  (lambda ()
    (when (not plot-frame)
      (set! plot-frame (new frame% [label "Elite 3D"] [width 600] [height 600]))
      (set! plot-canvas (new plot-canvas% [parent plot-frame]))
      (send plot-frame show #t))))

; Grafica solo los puntos 3D en la ventana reutilizable
(define graficar-puntos-3d
  (lambda (pts)
    (ensure-plot-window)
    (set! last-renderer3d (list (points3d (pts->vectors pts)
                                          #:sym 'fullcircle #:size 6 #:color "blue")))
    (send plot-canvas refresh)))

; Grafica la superficie de un árbol y (opcionalmente) sobrepone puntos
(define graficar-arbol-superficie
  (lambda (arbol pts)
    (ensure-plot-window)
    (call-with-values
      (lambda () (rangos-xy pts))
      (lambda (xmin xmax ymin ymax)
        (set! last-renderer3d
              (list (surface3d (convertir-arbol-a-funcion arbol)
                               xmin xmax ymin ymax)
                    (points3d (pts->vectors pts)
                              #:sym 'fullcircle #:size 6 #:color "red")))
        (send plot-canvas refresh)))))

; Algoritmo genético con actualización de plot cada k generaciones
(define algoritmo-genetico-con-plot
  (lambda (PG n k pts)
    (define resultado (box #f))
    (define (actualizar-plot elite generacion)
      (queue-callback
       (lambda ()
         (graficar-arbol-superficie elite pts)
         (printf "[plot] gen ~a | cercania ~a\n" generacion (cercaniaXYZ elite)))
       #t))
    (define worker
      (thread
       (lambda ()
         (let loop ([pg PG] [restantes n])
           (define fits (get-fitness pg))
           (define elite (escogerElite pg fits (apply max fits)))
           (define generacion (- n restantes))
           (when (or (zero? generacion) (zero? (remainder generacion k)))
             (actualizar-plot elite generacion))
           (cond
             ((zero? restantes) (set-box! resultado elite))
             ((>= 0.05 (cercaniaXYZ elite)) (set-box! resultado elite))
             (else (loop (generar-descendencia (list elite) pg (- (length pg) 1) fits)
                         (- restantes 1))))))))
    (thread-wait worker)
    (unbox resultado)))

; Variante asíncrona: inicia el proceso y devuelve una caja con el resultado final cuando esté listo
(define algoritmo-genetico-con-plot/async
  (lambda (PG n k pts)
    (define resultado (box #f))
    (define (actualizar-plot elite generacion)
      (queue-callback
       (lambda ()
         (graficar-arbol-superficie elite pts)
         (printf "[plot-async] gen ~a | cercania ~a\n" generacion (cercaniaXYZ elite)))
       #t))
    (thread
     (lambda ()
       (let loop ([pg PG] [restantes n])
         (define fits (get-fitness pg))
         (define elite (escogerElite pg fits (apply max fits)))
         (define generacion (- n restantes))
         (when (or (zero? generacion) (zero? (remainder generacion k)))
           (actualizar-plot elite generacion))
         (cond
           ((zero? restantes) (set-box! resultado elite))
           ((>= 0.05 (cercaniaXYZ elite)) (set-box! resultado elite))
           (else (loop (generar-descendencia (list elite) pg (- (length pg) 1) fits)
                       (- restantes 1)))))))
    resultado))


;(graficar-puntos-3d pts)
;(define gen (generarPoblacion 100))
;(define mejor (algoritmo-genetico-con-plot gen 300 10 pts))

; abrir/reusar ventana
(graficar-puntos-3d pts)

; correr en segundo plano, actualizando cada 10 gens
(define gen (generarPoblacion 100))
(define res (algoritmo-genetico-con-plot/async gen 300 10 pts))
(graficar-arbol-superficie res pts)
; cuando termine:
(unbox res)