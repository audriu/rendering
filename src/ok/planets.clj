(ns ok.planets
  (:require [quil.core :as q]
            [quil.middleware :as m]))
;Ported from https://pastebin.com/wzBHe3au

; The gravitational constant G
(def G 6.67428e-11)

; Assumed scale: 100 pixels = 1AU
(def AU (* 149.6e6 1000))
(def SCALE (/ 250 AU))
;Half a day
(def timestep (* 12 3600))
(def global-width 100)
(def global-heigth 100)

(deftype body [id px py vx vy mass radius color])

(defn force [g mass otherMass distance]
  ;Calculate the force of attraction
  (/ (* g (* mass otherMass)) (Math/pow distance 2)))

(defn directionOfForce [dx dy force]
  ;Calculate direction of the force
  (let [theta (Math/atan2 dy dx)]
    (list (* (Math/cos theta) force) (* (Math/sin theta) force))))

(defn attraction [body otherBody]
  ;Creates a vector to adjust planet heading depending on all other bodies
  (let [dx (- (.-px otherBody) (.-px body))
        dy (- (.-py otherBody) (.-py body))
        ;Distance between bodies
        distance (Math/sqrt (+ (Math/pow dx 2) (Math/pow dy 2)))]
    (if (= distance 0) (print "Hitt!")
                       (directionOfForce dx dy
                                         (force G (.-mass body) (.-mass otherBody) distance)))))

(defn totalAttraction [body bodies fxy]
  ;Creates a list of vectors, a vector for every body
  (if (empty? bodies)
    fxy
    (totalAttraction body (rest bodies) (map + fxy (attraction body (first bodies))))))

(defn gravity [bodies timestep]
  (let* [forces (for [b bodies] (totalAttraction b (remove b bodies) '(0 0)))
         vectors (for [f forces b bodies] (list (+ (.-vx b) (* (/ (first f) (.-mass b)) timestep))
                                                (+ (.-vy b) (* (/ (first (rest f)) (.-mass b)) timestep))))
         positions (for [v vectors b bodies] (list (+ (.-px b) (* (first v) timestep))
                                                   (+ (.-py b) (* (first (rest v)) timestep))))]
    (for [b bodies v vectors p positions]
      (->body (.-id b) (first p) (first (rest p)) (first v) (first (rest v))
            (.-mass b) (.-radius b) (.-color b)))))


;A list of bodies, size of planets is not real... you woldent se the planets.
(def testCollPlanets (list
                       ;(body "Havoc" (* -1.2 AU) 0 0 (* -10 1000) (* 8 (expt 10 25)) 50 "green")
                       (->body "Sun" 0 0 0 0 (* 1.98892 (Math/pow 10 30)) 100 "yellow")
                       (->body "Mercury" (* -0.387098 AU) 0 0 (* -47.362 1000) (* 3.3011 (Math/pow 10 23)) 4 "red")
                       (->body "Venus" (* 0.723 AU) 0 0 (* 35.02 1000) (* 4.8685 (Math/pow 10 24)) 8 "brown")
                       (->body "Earth" (* -1 AU) 0 0 (* -29.783 1000) (* 5.9742 (Math/pow 10 24)) 8 "green")
                       (->body "Mars" (* -1.5236 AU) 0 0 (* -24.077 1000) (* 6.4174 (Math/pow 10 23)) 4 "orange")))

(defn printBodies [bodies scale]
  ;To print the numbers for control
  (if (empty? bodies)
    (printf "Done\n")
    (let [p (printf "Position XY ~a \n" (list (.-id (first bodies))
                                              (* (.-px (first bodies)) scale)
                                              (* (.-py (first bodies)) scale)
                                              (* (.-vx (first bodies)) scale)
                                              (* (.-vy (first bodies)) scale)))]
      (printBodies (rest bodies) scale))))

(defn loop- [grav bodies timestep scale n]
  ;A numeric simulation
  (printBodies bodies scale)
  (if (> n 0)
    (loop- grav (gravity bodies timestep) timestep scale (- n 1))
    (printf "End")))


;(loop- G testCollPlanets timestep SCALE 90)

(defn setup []
  ; Set frame rate to 30 frames per second.
  (q/frame-rate 30)
  ; Set color mode to HSB (HSV) instead of default RGB.
  (q/color-mode :hsb)
  ; setup function returns initial state. It contains
  ; circle color and position.
  {:color  0
   :x      100
   :y      100
   :xspeed 2
   :yspeed 5})

(defn update-state [state]
  (let [x (:x state)
        y (:y state)
        xspeed (:xspeed state)
        yspeed (:yspeed state)
        x (+ x xspeed)
        y (+ y yspeed)]
    {:color  (mod (+ (:color state) 0.7) 255)
     :x      x
     :y      y
     :xspeed (if (or (< x 0) (> x global-width))
               (* -1 xspeed)
               xspeed)
     :yspeed (if (or (< y 0) (> y global-heigth))
               (* -1 yspeed)
               yspeed)}))

(defn draw-state [bodies]
  ;Update planet positions and paint
  (q/background 240)
  (let [bp (gravity bodies timestep)]
    (for [b bp i (count bodies)]
      ;mutate struct
      (do
        (set! (.-px b) (nth testCollPlanets i) (.-px b))
        (set! (.-py b) (nth testCollPlanets i) (.-py b))
        (set! (.-vx b) (nth testCollPlanets i) (.-vx b))
        (set! (.-vy b) (nth testCollPlanets i) (.-vy b))
        (q/fill (.-color b) 255 255)
        (q/ellipse
          (+ (* (.-px b) SCALE) (- 500 (/ (.-radius b) 2)))
          (+ (* (.-py b) SCALE) (- 500 (/ (.-radius b) 2)))
          (.-radius b)
          (.-radius b))))))

(q/defsketch ok
             :title "Solarsystem simulator"
             :size [global-width global-heigth]
             ; setup function called only once, during sketch initialization.
             :setup setup
             ; update-state is called on each iteration before draw-state.
             :update update-state
             :draw draw-state
             :features [:keep-on-top]
             ; This sketch uses functional-mode middleware.
             ; Check quil wiki for more info about middlewares and particularly
             ; fun-mode.
             :middleware [m/fun-mode])
