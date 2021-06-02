package util

import (
	"encoding/json"
	"io"
	"io/ioutil"
)

func Bodytojson(w io.Reader) (map[string]interface{}, error) {
	var o map[string]interface{}
	body, err := ioutil.ReadAll(w)
	if err != nil {
		return o, err
	}

	json.Unmarshal(body, &o)
	return o, nil
}
