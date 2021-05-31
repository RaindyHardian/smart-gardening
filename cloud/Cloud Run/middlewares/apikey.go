package middlewares

import (
	"net/http"

	"bangkit.academy/smartgardening/cloudrun/util"
	"github.com/gin-gonic/gin"
)

type Header struct {
	APIKey string `header:"x-api-key" binding:"required"`
}

func APIKey() gin.HandlerFunc {
	return func(c *gin.Context) {
		h := Header{}
		if err := c.ShouldBindHeader(&h); err != nil {
			c.JSON(http.StatusBadRequest, gin.H{
				"detail": err.Error(),
				"error":  "API Key not defined",
			})
			c.Abort()
			return
		}
		if err := util.VerifyKey(h.APIKey); err != nil {
			c.JSON(http.StatusUnauthorized, gin.H{
				"error": err.Error(),
			})
			c.Abort()
		}
		c.Next()
	}
}
