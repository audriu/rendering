(ns ok.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(def global-width 600)
(def global-heigth 600)

(defn setup []
  ; Set frame rate to 30 frames per second.
  (q/frame-rate 30)
  ; Set color mode to HSB (HSV) instead of default RGB.
  (q/color-mode :hsb)
  ; setup function returns initial state. It contains
  ; circle color and position.
  {:color 0
   :x 100
   :y 100
   :xspeed 2
   :yspeed 5})

(defn update-state [state]
  (let [x (:x state)
        y (:y state)
        xspeed (:xspeed state)
        yspeed (:yspeed state)
        x (+ x xspeed)
        y (+ y yspeed)
        ]
    {:color (mod (+ (:color state) 0.7) 255)
     :x x
     :y y
     :xspeed (if (or (< x 0)(> x global-width))
               (* -1 xspeed)
               xspeed)
     :yspeed (if (or (< y 0)(> y global-heigth))
               (* -1 yspeed)
               yspeed)
}))

(defn draw-state [state]
  ; Clear the sketch by filling it with light-grey color.
  (q/background 240)
  ; Set circle color.
  (q/fill (:color state) 255 255)
  ; Calculate x and y coordinates of the circle.
  (let [x (:x state)
        y (:y state)]
          (q/ellipse x y 100 100)))


(q/defsketch ok
  :title "Vectors"
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
