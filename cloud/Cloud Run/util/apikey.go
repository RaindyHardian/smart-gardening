package util

import (
	"errors"

	"bangkit.academy/smartgardening/cloudrun/setting"
)

func VerifyKey(key string) error {
	if key != setting.ServerSetting.APIKey {
		return errors.New("API Key not valid")
	}
	return nil
}
