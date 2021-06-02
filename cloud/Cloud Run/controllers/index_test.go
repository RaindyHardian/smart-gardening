package controllers_test

import (
	"net/http"
	"net/http/httptest"
	"testing"

	"bangkit.academy/smartgardening/cloudrun/routers"
	"bangkit.academy/smartgardening/cloudrun/setting"
	"bangkit.academy/smartgardening/cloudrun/util"
	"github.com/gin-gonic/gin"
	"github.com/stretchr/testify/assert"
)

func init() {
	gin.SetMode(gin.ReleaseMode)
	setting.Setup()
}

var r = routers.Setup()

func TestIndex_No_ApiKey(t *testing.T) {
	bodyExpected := map[string]interface{}{
		"detail": "Key: 'Header.APIKey' Error:Field validation for 'APIKey' failed on the 'required' tag",
		"error":  "API Key not defined",
	}
	w := httptest.NewRecorder()
	req, _ := http.NewRequest("GET", "/", nil)
	r.ServeHTTP(w, req)

	bodyResult, err := util.Bodytojson(w.Body)
	if err != nil {
		t.Fatal(err)
	}

	assert.Equal(t, 400, w.Code)
	assert.Equal(t, bodyExpected, bodyResult)
}

func TestIndex_Invalid_Apikey(t *testing.T) {
	bodyExpected := map[string]interface{}{
		"error": "API Key not valid",
	}
	w := httptest.NewRecorder()
	req, _ := http.NewRequest("GET", "/", nil)
	req.Header.Add("x-api-key", "a")
	r.ServeHTTP(w, req)

	bodyResult, err := util.Bodytojson(w.Body)
	if err != nil {
		t.Fatal(err)
	}

	assert.Equal(t, 401, w.Code)
	assert.Equal(t, bodyExpected, bodyResult)
}

func TestIndex_Valid_Apikey(t *testing.T) {
	bodyExpected := map[string]interface{}{
		"message": "Bangkit Capstone B21-CAP0251",
	}
	w := httptest.NewRecorder()
	req, _ := http.NewRequest("GET", "/", nil)
	req.Header.Add("x-api-key", setting.ServerSetting.APIKey)
	r.ServeHTTP(w, req)

	bodyResult, err := util.Bodytojson(w.Body)
	if err != nil {
		t.Fatal(err)
	}

	assert.Equal(t, 200, w.Code)
	assert.Equal(t, bodyExpected, bodyResult)
}
