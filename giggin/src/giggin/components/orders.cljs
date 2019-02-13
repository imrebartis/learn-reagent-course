(ns giggin.components.orders
  (:require [giggin.state :as state]
            [giggin.helpers :refer [format-price]]))

(defn total
  []
  ; thread first macro (i.e. we place the reduce function to the end + the first argument is @state/orders)
  ; this is like a pipe transfromation: it takes @state/orders, it maps it over & then it reduces it
  (->> @state/orders
      (map (fn [[id quant]] (* quant (get-in @state/gigs [id :price]))))
      (reduce + )))
  ; thread last macro (i.e. we place the reduce function to the beginning + the last argument is @state/orders)
  ; (reduce + (map (fn [[id quant]] (* quant (get-in @state/gigs [id :price]))) @state/orders)))

(defn orders
  []
  [:aside
    (if (empty? @state/orders)
      [:div.empty
        [:div.title "You don't have any orders"]
        [:div.subtitle "Click on a + to add an order"]])
      [:div.order
        [:div.body
          (for [[id quant] @state/orders]
            [:div.item {:key id}
              [:div.img
                [:img {:src (get-in @state/gigs [id :img])
                       :alt (get-in @state/gigs [id :title])}]]
               [:div.content
               ; string concatenation
                [:p.title (str (get-in @state/gigs [id :title]) " \u00D7 " quant)]]
               [:div.action
                ; multiply price with quant
                [:div.price (format-price (* (get-in @state/gigs [id :price]) quant))]
                [:button.btn.btn--link.tooltip
                  {:data-tooltip "Remove"
                   :on-click (fn [] (swap! state/orders dissoc id))}
                   [:i.icon.icon--cross]]]])]
          [:div.total
            [:hr]
            [:div.item
              [:div.content "Total"]
              [:div.action
              ; below we call the total function
                [:div.price (format-price (total))]]
              [:button.btn.btn--link.tooltip
                {:data-tooltip "Remove all"
                ; the {} below is an empty map
                 :on-click (fn [] (reset! state/orders {}))}
                [:i.icon.icon-delete]]]
        ]]])
