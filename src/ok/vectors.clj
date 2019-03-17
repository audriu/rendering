(ns ok.vectors)

(defn vec3-squared-length [[a1 a2 a3]]
  (+ (* a1 a1) (* a2 a2) (* a3 a3)))

(defn vec3-length [vec3]
  (Math/sqrt (vec3-squared-length vec3)))

(defn vec3-product [[a1 a2 a3] [b1 b2 b3]]
  [(* a1 b1) (* a2 b2) (* a3 b3)])

(defn vec3-plus [[a1 a2 a3] [b1 b2 b3]]
  [(+ a1 b1) (+ a2 b2) (+ a3 b3)])

(defn vec3-minus [[a1 a2 a3] [b1 b2 b3]]
  [(- a1 b1) (- a2 b2) (- a3 b3)])

(defn vec3-division [[a1 a2 a3] [b1 b2 b3]]
  [(/ a1 b1) (/ a2 b2) (/ a3 b3)])

(defn vec3-scalar-multiply [scalar vec3]
  (mapv #(* scalar %) vec3))

(defn vec3-dot-product [[a1 a2 a3] [b1 b2 b3]]
  (+ (* a1 b1) (* a2 b2) (* a3 b3)))

(defn vec3-cross-product [[a1 a2 a3] [b1 b2 b3]]
  [(- (* a2 b3) (* a3 b2)),
   (- (* a3 b1) (* a1 b3))
   (- (* a1 b2) (* a2 b1))])

(defn vec3-unit-vector [vec3]
  (vec (for [elem vec3]
         (/ elem (vec3-length vec3)))))

(defn create-ray [vec3-origin vec3-direction]
  {:origin vec3-origin
   :direction vec3-direction})

(defn ray-origin [ray]
  (:origin ray))

(defn ray-direction [ray]
  (:direction ray))

(defn point_at_parameter [{:keys [origin direction]} t]
  (vec3-plus origin
             (vec3-scalar-multiply t direction)))

(defn color [ray]
  (let [direction (ray-direction ray)
        y (get direction 1)
        t (* 0.5 (+ 1 y))
        white [1 1 1]
        blue  [0.5 0.7 1]]
    (vec3-plus (vec3-scalar-multiply (- 1 t) white)
               (vec3-scalar-multiply t blue))))

(let [nx     200
      ny     100
      lower-left-corner [-2 -1 -1]
      origin [0 0 0]
      horizontal [4 0 0]
      vertical [0 2 0]
      header (str "P3\n" nx " " ny "\n255\n")
      body   (clojure.string/join (for [j (range (dec ny) -1 -1)
                                        i (range 0 nx)
                                        :let  [u (/ i nx)
                                               v (/ j ny)
                                               vector-on-plane (vec3-plus (vec3-scalar-multiply u horizontal) (vec3-scalar-multiply v vertical))
                                               direction (vec3-plus lower-left-corner vector-on-plane)
                                               ray (create-ray origin direction)
                                               pixel-color (color ray)
                                               ir (int (* 255.99 (get pixel-color 0)))
                                               ig (int (* 255.99 (get pixel-color 1)))
                                               ib (int (* 255.99 (get pixel-color 2)))]]
                                    (str ir " " ig " " ib "\n")))]
  (spit "./output_images/image_3.ppm" header :append true)
  (spit "./output_images/image_3.ppm" body :append true))
