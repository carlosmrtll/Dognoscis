
from image_recognizer import ImageRecognizer
#from pymongo import MongoClient
from flask import *

import configparser
import json
import os

os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'

# Configuration file
abs_path = os.path.dirname(os.path.realpath(__file__)) + '/'
with open('config.json') as json_data_file:
    data = json.load(json_data_file)

app = Flask(__name__)

# MongoDB
#client = MongoClient(data['mongo_db']['address'], data['mongo_db']['port'])
#collection = client[data['mongo_db']['database']][data['mongo_db']['collection']]

# Tensor Flow
ir = ImageRecognizer(
    abs_path + data['tf_path'],
    data['tensor_flow']['graph'],
    data['tensor_flow']['labels'],
    data['tensor_flow']['input_layer_name'],
    data['tensor_flow']['output_layer_name'],
    data['tensor_flow']['num_top_predictions']
)

def build_full_results(partial_results):
    full_result = {}
    for k in partial_results:
        if k not in data['dog_data']:
            continue
        full_result[k] = {
            'value': partial_results[k],
        }
    return full_result

# Main and only route
@app.route('/upload', methods=['POST'])
def upload():
    if request.method == 'POST':
        file = request.files.get('fileupload')
        filename = file.filename
        file.save(abs_path + data['img_path'] + filename)

        results = ir.recognize(data['img_path'] + filename)
        results['image'] = filename
        print(filename)
        #collection.insert(results)
        #del results['_id']

        print(results)
        return jsonify(build_full_results(results))

if __name__ == '__main__':
        app.run(host=data['server']['address'], port=data['server']['port'])
