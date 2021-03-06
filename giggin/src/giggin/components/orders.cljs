(ns giggin.components.orders
  (:require [giggin.state :as state]
            [giggin.helpers :refer [format-price]]
            [giggin.components.checkout-modal :refer [checkout-modal]]))

(defn total
  []
  ; thread first macro (i.e. we place the reduce function to the end + the first argument is @state/orders)
  ; this is like a pipe transfromation: it takes @state/orders, it maps it over & then it reduces it
  (->> @state/orders
      (map (fn [[id quant]]
        (if (get-in @state/gigs [id :sold-out])
         0
         (* quant (get-in @state/gigs [id :price])))))
      (reduce + )))
  ; thread last macro (i.e. we place the reduce function to the beginning + the last argument is @state/orders)
  ; (reduce + (map (fn [[id quant]] (* quant (get-in @state/gigs [id :price]))) @state/orders)))

; // DIFFERENT WAYS TO WRITE FUNCTIONS
; (defn greet-1 [name] (str "Hello " name))
; (def greet-2 (fn [name] (str "Hello " name)))
; (def greet-3 #(str "Hello " %))
;
; (def greet-4 (fn [name lastname] (str "Hello " name lastname)))
; (def greet-5 #(str "Hello " %1 %2))
; (greet-1 "Macek")
; (greet-2 "Hasek")
; (greet-3 "Mrozek")
; (greet-4 "Shlobert" " Swobodloi")

(defn orders
  []
  (let [remove-from-order #(swap! state/orders dissoc %)
        remove-all-orders #(reset! state/orders {})]
    [:aside
      (if (empty? @state/orders)
        [:div.empty
          [:div.title "You don't have any orders"]
          [:div.subtitle "Click on a + to add an order"]])
        [:div.order
          [:div.body
            (doall (for [[id quant] @state/orders]
                    (let [gig (id @state/gigs)]
                      [:div.item {:key id}
                        [:div.img
                          [:img {:src (:img gig)
                                 :alt (:title gig)}]]
                         [:div.content
                          (if (:sold-out gig)
                            [:p.sold-out "Sold out"]
                           ; string concatenation
                            [:p.title (:title gig) " \u00D7 " quant])]
                         [:div.action
                          (if (:sold-out gig)
                            [:div.price (format-price 0)]
                          ; multiply price with quant
                            [:div.price (format-price (* (:price gig) quant))])
                          [:button.btn.btn--link.tooltip
                            {:data-tooltip "Remove"
                             :on-click #(remove-from-order id)}
                             [:i.icon.icon--cross]]]])))]
          [:div.total
            [:hr]
            [:div.item
              [:div.content "Total"]
              [:div.action
              ; below we call the total function
                [:div.price (format-price (total))]]
              [:button.btn.btn--link.tooltip
                {:data-tooltip "Remove all"
                 :on-click #(remove-all-orders)}
                [:i.icon.icon--delete]]]
                [checkout-modal]
        ]]]))
