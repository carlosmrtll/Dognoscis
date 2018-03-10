from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

import argparse
import sys

import tensorflow as tf

class ImageRecognizer:
    def __init__(self, path, graph, labels, input_layer_name, output_layer_name, num_top_predictions):
        self.path = path
        self.graph = graph
        self.labels = labels
        self.input_layer_name = input_layer_name
        self.output_layer_name = output_layer_name
        self.num_top_predictions = num_top_predictions

    def load_image(self, filename):
      """Read in the image_data to be classified."""
      return tf.gfile.FastGFile(filename, 'rb').read()

    def load_labels(self, filename):
      """Read in labels, one label per line."""
      return [line.rstrip() for line in tf.gfile.GFile(filename)]

    def load_graph(self, filename):
      """Unpersists graph from file as default graph."""
      with tf.gfile.FastGFile(filename, 'rb') as f:
        graph_def = tf.GraphDef()
        graph_def.ParseFromString(f.read())
        tf.import_graph_def(graph_def, name='')


    def run_graph(self, image_data, labels, input_layer_name, output_layer_name,
                  num_top_predictions):
      #print ("r_g()")
      with tf.Session() as sess:
        # Feed the image_data as input to the graph.
        #   predictions will contain a two-dimensional array, where one
        #   dimension represents the input image count, and the other has
        #   predictions per class
        softmax_tensor = sess.graph.get_tensor_by_name(output_layer_name)
        predictions, = sess.run(softmax_tensor, {input_layer_name: image_data})

        # Sort to show labels in order of confidence
        top_k = predictions.argsort()[-self.num_top_predictions:][::-1]
        values = {}
        #print ("run_graph() 2")
        for node_id in top_k:
          human_string = labels[node_id]
          score = predictions[node_id].item()
          print('%s (score = %.5f)' % (human_string, score))
          values[human_string] = score
        return values

    def recognize(self, image):
        image_data = self.load_image(image)
        labels = self.load_labels(self.path + self.labels)
        self.load_graph(self.path+ self.graph)
        #print ("recognize()")

        return self.run_graph(image_data, labels, self.input_layer_name, self.output_layer_name,
                  self.num_top_predictions)
