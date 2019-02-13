(ns giggin.helpers)

  (defn format-price
    ; divide cents by 100
    [cents]
    (str " €" (/ cents 100)))
