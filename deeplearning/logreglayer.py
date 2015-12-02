import numpy as np                  # For vector operations

import theano                       # Deep learning library
import theano.tensor as T


class LogRegLayer(object):
    ''' Logistic regression layer class

            This class represents the logistic regression layer in the neural network.

            Parameters:

                input    -  A vector wich is an another hidden layer's output

                n_in     -  An integer which represents the layer's input  length

                n_out    -  An integer which represents the layer's output length

                W        -  The layer's weight vector

                b        -  The layer's bias vector

                output   -  The layer's output vector
    '''

    def __init__(self, input_, n_in, n_out):
        
        # If @n_in is lesser than @n_out then we write out an error message and terminate the program    
        if n_in < n_out:
            print 'Error at LogRegLayer __init__: n_in < n_out!'
            exit(1)

        self.input  = input_

        self.W 	    = None
        self.b      = None
        self.output = None

        rng = np.random.RandomState()

        # Creating the weight and bias vectors
        W_values = np.zeros((n_in, n_out), dtype=theano.config.floatX)
        b_values = np.zeros(n_out, dtype=theano.config.floatX)

        self.W = theano.shared(value=W_values, name='W', borrow=True)
        self.b = theano.shared(value=b_values, name='b', borrow=True)

        # Generating the output vector
        self.output = T.nnet.softmax(T.dot(self.input, self.W) + self.b)

    def setW(self, W, b):
        self.W = theano.shared(value=W, name='W', borrow=True)
        self.b = theano.shared(value=b, name='b', borrow=True)

        self.output = T.tanh(T.dot(self.input, self.W) + self.b)