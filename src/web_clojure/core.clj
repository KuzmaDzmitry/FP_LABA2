(ns web-clojure.core
  (:gen-class)
  (:require [org.httpkit.client :as httpkit]
            [net.cgrand.enlive-html :as html]))

(def Good-Url (atom '()))
(def Bad-Url (atom '()))
(def Redir-Url (atom '()))
(def All-Url (atom '()))
(defn get-code-content [url]
   (let [response @(httpkit/get url {:follow-redirects false :throw-exceptions false})
         status (response :status)]
         (cond 
           (= 200 status) [:ok]
           (= 404 status) [:not-found]
           (contains? #{301 302 307 400} status) [:redirect]
           :else [:unknown-error] )
         ))
(defn get-links-page [page-body]
  (let [tags-a (html/select page-body [:a])
        links (map #(get-in % [:attrs :href]) tags-a)]
    links)
  )
(defn links-filter [links]
    (->> links
    (filter identity)
    (filter #(.startsWith % "http")))
  )
(defn links-collector [url]
  (let [content (html/html-resource (java.net.URL. url))
        links (get-links-page content)
        friendly-links ( links-filter links)]
        friendly-links))


(defn links-runner [url counter]
    (let [[stat] (get-code-content url)
          rez (hash-map :link url :status stat)]
          (println url)
          (swap! All-Url conj rez)
          (case stat
            :ok (let [links (links-collector url)]
               ; (println links)
                 (swap! Good-Url conj rez)
                 (if (= counter 1) 
                  ()
                   (doall (pmap (fn [link]
                          (links-runner link (inc counter) ))
                          links))
                  )
                 )
            :not-found (swap! Bad-Url conj rez)  
            :redirect  (swap! Redir-Url conj rez)
            :unknown-error (swap! Bad-Url conj rez)
            )
    )
  )
(defn -main [& args]
   (let [url "http://www.dys-add.com"
         counter 0]
    (links-runner url counter)
    (println "Good-Url")
    (println (count @Good-Url))
    (println "Bad-Url")
    (println (count @Bad-Url))
    (println "Redir-Url")
    (println (count @Redir-Url))
    (println "All-Url")
    (println (count @All-Url))
	)
)
