# Usage: python app.py
from werkzeug.middleware.shared_data import SharedDataMiddleware
from flask import send_from_directory
import os

from flask import Flask, render_template, request, redirect, url_for
from werkzeug.utils import secure_filename
from keras.preprocessing.image import ImageDataGenerator, load_img, img_to_array
from keras.models import Sequential, load_model
import numpy as np
import time
import uuid
import base64

img_width, img_height = 150, 150
model_path = '../smart-gardening/ml/6_model_epoch_360_akurasi_73_71.h5'
model = load_model(model_path)
# model.load_weights(model_weights_path)

UPLOAD_FOLDER = 'uploads'
ALLOWED_EXTENSIONS = set(['jpg', 'jpeg'])


def get_as_base64(url):
    return base64.b64encode(request.get(url).content)


def predict(file):
    img = load_img(file, target_size=(75, 100))
    img = img_to_array(img)
    img = np.expand_dims(img, axis=0)
    result = model.predict(img)
    print(result)

    y_pred = np.argmax(result, axis=1)

    if y_pred[0] == 0:
        print("aglonema")
    elif y_pred[0] == 1:
        print("janda_bolong")
    elif y_pred[0] == 2:
        print("kuping_gajah")
    elif y_pred[0] == 3:
        print("lidah_mertua")
    elif y_pred[0] == 4:
        print("sirih_gading")
    elif y_pred[0] == 5:
        print("tanaman_lipstik_gantung")
    elif y_pred[0] == 6:
        print("tanaman_suplir")
    return y_pred


def my_random_string(string_length=10):
    """Returns a random string of length string_length."""
    random = str(uuid.uuid4())  # Convert UUID format to a Python string.
    random = random.upper()  # Make all characters uppercase.
    random = random.replace("-", "")  # Remove the UUID '-'.
    return random[0:string_length]  # Return the random string.


def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1] in ALLOWED_EXTENSIONS


app = Flask(__name__)
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER


@app.route("/")
def template_test():
    return render_template('template.html', label='', imagesource='../uploads/template.jpg')


@app.route('/', methods=['GET', 'POST'])
def upload_file():
    if request.method == 'POST':
        import time
        start_time = time.time()
        file = request.files['file']

        if file and allowed_file(file.filename):
            filename = secure_filename(file.filename)

            file_path = os.path.join(app.config['UPLOAD_FOLDER'], filename)
            file.save(file_path)
            result = predict(file_path)

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
            print(result)
            print(file_path)
            filename = my_random_string(6) + filename

            os.rename(file_path, os.path.join(
                app.config['UPLOAD_FOLDER'], filename))
            print("--- %s seconds ---" % str(time.time() - start_time))
            return render_template('template.html', label=label, imagesource='../uploads/' + filename)


@app.route('/uploads/<filename>')
def uploaded_file(filename):
    return send_from_directory(app.config['UPLOAD_FOLDER'],
                               filename)


app.add_url_rule('/uploads/<filename>', 'uploaded_file',
                 build_only=True)
app.wsgi_app = SharedDataMiddleware(app.wsgi_app, {
    '/uploads':  app.config['UPLOAD_FOLDER']
})

if __name__ == "__main__":
    app.debug = False
    app.run(host='0.0.0.0', port=5000)
