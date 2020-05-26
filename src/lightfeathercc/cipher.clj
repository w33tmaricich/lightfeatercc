(ns lightfeathercc.cipher)

(defn encode-char [item shift]
  "Encodes a single character"
  (let [c (int item)
        ascii (mod (+ c shift) 126)]
    (char ascii)))

(defn encode
  [string shift]
  "Encrypts a string using a shift cypher."
  (let [ca (char-array string)
        ca-encoded (doall (map (fn [c] (encode-char c shift)) ca))]
    (clojure.string/join ca-encoded)))

(defn decode
  [string shift]
  "Decodes the encrypted string."
  (encode string (* shift -1)))
