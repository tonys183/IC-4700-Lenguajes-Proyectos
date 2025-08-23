#lang scheme

;==== Funciones auxiliares ====;

;---- & Manejo de arboles & ----;
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
      ((null? A) (list))
      ((zero? n) (list car A))
      ((list? A) (append (nivel (cadr A) (- n 1)) (nivel (caddr A) (- n 1))))
      (else (list)))))

;--- Reconstruir ---;

(define recons-inv-Arb
  (lambda(A)
    (cond
      ((or (null? A) (not (list? A))) A)
      (else (list (car A) (recons-inv-Arb (caddr A)) (recons-inv-Arb (cadr A))))
      )))



;---- & Manejo de listas &- ---;
; Dominio:
; Codominio:
(define elementoEn
  (lambda (L n)
    (cond
      ((zero? n) (car L))
      (else (elementoEn (cdr L) (- n 1))))))

(define pop
  (lambda (L)
    (cond
      ( (or (null? (cdr L)) (null? L) ) '())
      (else (cons (car L) (pop (cdr L)) ) ))))

(define top
  (lambda (L)
    (cond
      ((null? (cdr L)) (car L))
      (else (top (cdr L) )))))

(define primeros-n
  (lambda(L n)
    (cond
      ((or (null? L) (zero? n)) '())
      (else (cons (car L) (primeros-n (cdr L) (- n 1))))
    )))

 
;--- Auxiliares Números ---

(define precisionK
  (lambda(n k)
   (/ (round (* n (expt 10 k))) (expt 10 k))))

(define realRand
  (lambda()
    (cond
    ((< (random) 0.25) (precisionK (* -1 (* (random) (expt 10 (floor (* (random) 10))))) 6))
    (else (precisionK (* (random) (expt 10 (floor (* (random) 10)))) 6)))))
  
(define realPositiveRand
  (lambda()
    (precisionK (* (random) (expt 10 (floor (* (random) 10)))) 6)))
  



;--- Válidación de Operaciones ---;

(define divValida
  (lambda (a b)
    (cond
      ((= b 0) 'error)
      (else (/ a b)))))


(define expValida
  (lambda (b e)
    (cond
      ((and (zero? b) (< e 0)) 'error)
      ((and (< b 0) (not (integer? e)))
       (cond
         ((complex? (- (expt (- b) e))) 'error) ;Complex? no sirve del todo
            (else (- (expt (- b) e)))))
      (else (expt b e)))))

(define logValido
  (lambda (a b)
    (cond
      ((or (< a 0) (<= b 1)) 'error)
      (else (/ (log a) (log b))))))




  
;--- Definición de operadores ---;
(define opsValidas (list
                    (cons '+ +)
                    (cons '- -)
                    (cons '* *)
                    (cons '/ divValida)
                    (cons 'loga_b logValido)
                    (cons '** expValida)))




;--- Puntos y Operaciones Posibles ---

(define pts '((0 1 4) (1 3 5) (2 5 6)))
(define opsPosibles '('+ '- '* '/ 'loga_b '**))


;--- SET ---

(define crearSet
  (lambda () (list)))

(define insertarSet
  (lambda (S x)
    (cond
      ((member x S equal?) S)
      (else (cons x S)))))





;--- Manejo de Individuos ---

(define evalOps
  (lambda (paramOp op)
    (cond
      ((member 'error paramOp equal?) 'error)
      (else (apply op paramOp)))))


(define evalArbol
  (lambda (arbol x y)
    (cond
      ((number? arbol) arbol)
      ((symbol? arbol)
       (cond
         ((eq? arbol 'x) x)
         ((eq? arbol 'y) y)
         (else arbol)))
      (else (evalOps (map (lambda (subArb) (evalArbol subArb x y)) (cdr arbol)) (cdr (assoc (car arbol) opsValidas)))))))





;--- Proceso Genético ---;

(define cercaniaXYZ 
  (lambda (A)
    (cond
      ((member 'error (map (lambda (p)
                            (cond
                             ((eq? 'error (evalArbol A (car p) (cadr p))) 'error)
                             (else (expt (- (caddr p) (evalArbol A (car p) (cadr p))) 2)))) pts)) 'error)
      (else (apply + (map (lambda (p)
                            (cond
                             ((eq? 'error (evalArbol A (car p) (cadr p))) 'error)
                             (else (expt (- (caddr p) (evalArbol A (car p) (cadr p))) 2)))) pts))))))


(define totalGen
  (lambda (difs) ;Lista de cercaniaXYZ de todos los individuos
    (apply + (map (lambda (d) (cond
                                  ((equal? d 'error) (/ 1 1000))
                                  (else (/ 1 (+ 1 d))))) difs))))

(define fitness
  (lambda (difs total)
    (map (lambda (d) (cond
                      ((equal? d 'error) (/ (/ 1 1000) total))
                      (else (/ (/ 1 (+ 1 d)) total)))) difs)))


(define escogerElite
  (lambda (S F max)
    (cond
      ((= (car F) max) (car S))
      (else (escogerElite (cdr S) (cdr F) max)))))




;==== Auxiliares para Mutación ====;

;--- Elegir un numero random de las hojas---;
(define elegirNumRand
  (lambda(A)
    (elementoEn (filter number? (nivel A (altura A))) 
          (random (length (filter number? (nivel A (altura A))))) )
        ))

;---Elegir elemento de hoja random---;
(define elegirHoja
  (lambda(A ruta)
    (cond
      ((or (null? A) (not (list? A))) (cons A ruta ))
      ((< (random) 0.5 ) (cons 0 (elegirHoja (cadr A) ruta)))
      (else  (cons 1 (elegirHoja (caddr A) ruta)))
      )))

; --- Meter un valor al arbol---;

(define cambiarElemento
  (lambda(A R e)
  (cond
    ((null? R) e)
    ((= 0 (car R)) (list (car A) (cambiarElemento (cadr A) (cdr R) e ) (caddr A)))
    (else (list (car A) (cadr A) (cambiarElemento (caddr A) (cdr R) e)))
  )))


(define swapHojas
  (lambda(A R1 R2)
    (cambiarElemento (cambiarElemento A (pop R2) (top R1)) (pop R1) (top R2))
  ))

;--- Cambiar Operador ---;
(define cambiarOp
  (lambda (A nvl op)
    (cond
      ((null? A) A)
      ((zero? nvl) (list op (cadr A) (caddr A)))
      ((and (not (list? (cadr A))) (not (list? (caddr A))) 
            (not (member (cadr A) opsPosibles equal?)) (not (member (caddr A) opsPosibles equal?)))
        (list op (cadr A) (caddr A)))
      ((<= (random) 0.5) (list (car A) (cambiarOp (cadr A) (- nvl 1) op) (caddr A)))
      (else (list (car A) (cadr A) (cambiarOp (caddr A) (- nvl 1) op)))
    )))


;--- Cambiar Número ---;
(define cambiarNum
  (lambda (A nvl nuevoNum hoja)
  (cond 
      ((null? A) A)
      ((zero? nvl) (cond ((equal? A hoja) nuevoNum) 
                         (else A)))
      ((list? A) (list (car A) (cambiarNum (cadr A) (- nvl 1) nuevoNum hoja) 
                               (cambiarNum (caddr A) (- nvl 1) nuevoNum hoja)))
      (else A))))


;==== Auxiliares para Supermutación ====;

(define quitar-rama-aux
  (lambda(A ruta)
    (cond
      ((null? A) '()) 
      ((null? ruta) (cond ((< (random) 0.50) (realRand)) ((< (random) 0.75) 'y)  (else 'x)))
      ((= 0 (car ruta)) (list (car A)  (quitar-rama-aux (cadr A) (cdr ruta)) (caddr A)))
      (else (list (car A) (cadr A) (quitar-rama-aux (caddr A) (cdr ruta))) )
    )))

(define quitar-rama
  (lambda(A ruta)
  (quitar-rama-aux A (primeros-n ruta (+ 1 (random (- (length ruta) 2))) ) )
))


(define pegar-subArbol
  (lambda(A ruta)
    (cond 
      ((null? ruta) (arbol-aleatorio  (+ 1 (random  3)) (+ 1 (random  2))))
      ((zero? (car ruta )) (list (car A) (pegar-subArbol  (cadr A) (cdr ruta)) (caddr A)))
      (else (list (car A) (cadr A) (pegar-subArbol  (caddr A) (cdr ruta))))
    )))

; ==== GENERACION ALEATORIA DE ARBOLES === ;

(define hoja-aleatoria
  (lambda(p)
    (cond
        ((< p 0.33334) (realPositiveRand) ) ; Solo positivos para evitar las indefiniciones por completo.
        ((< p 0.66667) 'x)
        (else 'y)
    )))

(define arbol-altura-n 
  (lambda(n)
    (cond
      ((zero? n) (hoja-aleatoria (random)))
      (else (list (elementoEn opsPosibles (random (length opsPosibles))) (arbol-altura-n (- n 1)) (arbol-altura-n (- n 1))))
    )))

(define arbol-aleatorio
  (lambda(n m) ; n es la cantidad de subarboles que quiero, m es la altura maxima de cada subarbol m<-N ^ m<-[1,+oo[. (m!=0).
    (cond
      ((= n 1) (arbol-altura-n (+ 1 (random m))))
      (else (list (elementoEn opsPosibles (random (length opsPosibles))) (arbol-aleatorio (- n 1) m) (arbol-aleatorio (- n 1) m)))
))) 



;==== Mutaciones y cruces ====;

;--- Supermutar ---;
(define supermutar
  (lambda (A p)
    (cond
      ((< p 0.25) (quitar-rama A (elegirHoja A '()))) ; Agarra una rama y la remplaza por un valor 'x,'y o n / n <- R
      ((< p 0.5) (recons-inv-Arb A)) ; Cambia cada valor, y cada rama derecha por su izquierda, y viceveresa.
      (else (pegar-subArbol A (pop (elegirHoja  A '())))) ; Añade un arbol aletorio al arbol, remplaza una hoja para hacerlo.
    )))




;--- Mutar ---;
(define mutar
  (lambda (A p)
    (cond
      ((< p 0.25) (cambiarOp A (random (altura A)) (elementoEn opsPosibles (random (length opsPosibles))) ))
      ((< p 0.5) (cambiarNum A (altura A) (realRand) (elegirNumRand A)))
      ((< p 0.75) (swapHojas A (elegirHoja A '()) (elegirHoja A '())))
      (else (cambiarOp (cambiarNum A (altura A) (realRand) (elegirNumRand A)) (random (altura A)) (elementoEn opsPosibles (random (length opsPosibles)))))
    )))



;--- Cruce ---;
(define cruces
  (lambda (A B)
    (cond
      ((< (random) 0.01) (supermutar (list (car A) (cdr A) (cddr B))))
      ((< (random) 0.05) (mutar (list (car A) (cdr A) (cddr B)) (random))) 
      (else (list (car A) (cdr A) (cddr B))))))


;==== Probar Funciones ====;

(define arbol-completo '(+ (* (/ 10 y) (loga_b 8 2)) (** x 2)))

;--- Datos ---;
(define indiv '(A B C D E F G))
(define fits (fitness '(error 11.34 21.38 12.123 56.93 12.21 11.56) 0.3755023920535431))

(define nivelHojas (altura arbol-completo))
(define hojas (filter number? (nivel arbol-completo nivelHojas)))
(define hojaRand (elementoEn hojas (random (length hojas))))

        
;--- Funciones ---;
;(cambiarNum arbol-completo nivelHojas 5 hojaRand)
;(cambiarOp arbol-completo 1 '+)


;(apply + (fitness '(error 11.34 21.38 12.123 56.93 12.21 11.56) 0.3755023920535431))
;(escogerElite indiv fits (apply max fits))






;==== Población ====;

(define  generarPoblacion
  (lambda(n)
    (cond
      ((zero? n) '())
      (else (cons (arbol-aleatorio  (+ 1 (random  5)) (+ 1 (random  7))) (generarPoblacion  (- n 1))))
    )))

