package firestore

import (
	"context"
	"errors"
	"fmt"
	"log"
	"reflect"

	"bangkit.academy/smartgardening/cloudrun/setting"
	"cloud.google.com/go/firestore"
)

func CreateClient(ctx context.Context) *firestore.Client {
	projectID := setting.ServerSetting.GoogleProjectID

	client, err := firestore.NewClient(ctx, projectID)
	if err != nil {
		log.Fatalf("Failed to create firestore client: %v", err)
	}

	return client
}

func AddData(ctx context.Context, client *firestore.Client, document string, data interface{}) error {
	_, err := client.Collection(setting.ServerSetting.GoogleFirestoreCollection).Doc(document).Set(ctx, data)
	if err != nil {
		return fmt.Errorf("failed add data firestore: %v", err)
	}
	return nil
}

func GetDataPlant(ctx context.Context, client *firestore.Client, name string) (plant map[string]interface{}, err error) {
	docPlant, err := client.Collection(setting.ServerSetting.GoogleFirestoreCollection).Doc(name).Get(ctx)
	if err != nil {
		return nil, fmt.Errorf("firestore query plant: %v", err)
	}

	plant = docPlant.Data()

	if _, exist := plant["disease"]; !exist {
		return nil, errors.New("disease at data plant not found")
	}

	if reflect.TypeOf(plant["disease"].(interface{})).Kind() != reflect.Slice {
		return nil, errors.New("data disease not slice")
	}

	disease := []map[string]interface{}{}
	for _, s := range plant["disease"].([]interface{}) {
		docDisease, err := client.Collection("disease").Doc(s.(string)).Get(ctx)
		if err != nil {
			return nil, fmt.Errorf("firestore query disease: %v", err)
		}
		disease = append(disease, docDisease.Data())
	}
	plant["disease"] = disease

	return plant, nil
}
