# Usage: python app.py
from PIL import Image
from io import BytesIO
from flask import Flask, request, jsonify
from tensorflow.keras.preprocessing.image import img_to_array
from tensorflow.keras.models import load_model
import numpy as np
import os

model_path = 'model/5_model_akurasi_93_91_93.h5'
model = load_model(model_path)


def predict(file):
    img = Image.open(BytesIO(file))
    img = img.resize((160, 160), Image.ANTIALIAS)
    img = img_to_array(img)
    img = np.expand_dims(img, axis=0)
    result = model.predict(img)

    y_pred = np.argmax(result, axis=1)
    return y_pred


app = Flask(__name__)

@app.route("/")
def hello():
    return "Machine Learning Prediction Endpoint"

@app.route('/predict', methods=['POST'])
def upload_file():
    if request.method == 'POST':
        file = request.files['file'].read()
        result = predict(file)

        if result == 0:
            label = "Aglaonema"
        elif result == 1:
            label = "Janda Bolong"
        elif result == 2:
            label = "Kuping Gajah"
        elif result == 3:
            label = "Lidah Mertua"
        elif result == 4:
            label = "Sirih Gading"
        elif result == 5:
            label = "Tanaman Lipstik Gantung"
        elif result == 6:
            label = "Tanaman Suplir"
        else:
            label = "Data not found"

        return jsonify(result=label)


if __name__ == "__main__":
    app.run(debug=True, host='0.0.0.0', port=int(os.environ.get("PORT", 8080)))
