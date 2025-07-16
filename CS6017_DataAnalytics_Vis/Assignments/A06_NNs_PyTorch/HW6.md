# HW 6: Character classification using NNs with PyTorch

## Due July 26

In this assignment we'll tackle a slightly more complicated image classification problem than MNIST digit classification.  We're going to classify characters that contain (gasp!) letters!

The dataset we'll play with is from University of California, Irvine (UCI) and contains a bunch of images of letters of various fonts.  Some printed + scanned, some the values screen-capped from a computer.  The images are 20x20 pixels, grayscale.

## Step 1: Data acquisition + cleanup

Grab the dataset from [here](https://archive.ics.uci.edu/ml/datasets/Character+Font+Images) and unzip it.

The unzipped folder contains a CSV file for each of the various fonts in the data set.  Pick one to work with and load it into a pandas dataframe.

The data is unfortunately in a really, really horrible format for us.  We're going to throw away basically everything except for the m_label field (the unicode codepoint for the character, which is the same as the ascii value for basic alphanumeric characters) and the pixel values which are scattered across 400 columns labeled rxcy (where x and y are the row and column numbers that range from 0 to 19).

To Start, write a function that transforms a dataframe and returns 2 numpy arrays: Xs which is a #samples x 20 x 20 array containing the pixel values, and Ys which is a #samples x 1 array containing the ascii vales for each character.  You should normalize the Xs array so the values go from 0-1 (probably this requires dividing by 256 or using the scale function from A5)

For our labels, we'll need to do a little bit of preprocessing as well.  While we COULD use the ascii/unicode values of the characters as our label, this wouldn't work very well becuase we'd basically need to have outputs from 0 up to the max number, even though a lot of those won't have corresponding characters in the dataset.  This is especially true for fonts that have high unicode code-point characters in them.  To make our lives easier, we'll want to assign each character a smaller index value.  I suggest making dictionaries to convert from unicode number to label index and vice versa, which will be basically the same as we did for words in the NLP with Neural Nets code.

## Step 2: Build a Pytorch network

We're going to use the Pytorch library, like we've seen in class, to build/train our network.  Check out the notebooks we've made in class or the official documentation/tutorials.

To start with, we're going to use a model very similar to the MNIST CNN we used in class.  It will consist of:

* a Convolution2D layer with relu activations
* a max pooling layer
* another convolution layer
* another max pooling layer
* a dense layer with relu activation
* a dense layer


Compile and train your network like we did in class.  You'll probably have to use the np.reshape function on your data to make Pytorch happy.  I reshaped my X values like np.reshape(Xs, (-1, 1, 20, 20)) to get them in the right format.

For training, you'll want to check out `torch.utils.data.DataLoader` which can take a `TensorDataset` so you can iterate over batches like we did in class for the MNIST data.

## Step 3: Exploration and Evaluation

* Evaluate the network using cross validation (splitting data into training/testing).  What is its accuracy?
* Create and train a different network topology (add more convolution layers, experiment with normalization (batch normalization or dropout), explore other types/sizes of layer).  Try to find a topology that works better than the one described above.
* Test the accuracy of your network with character inputs from a DIFFERENT font set.  How does it perform?
* Train your best network on inputs from the data from at least 2 different fonts.  How does your accuracy compare to the 1-font case?  What accuracy do you see when testing with inputs from a font you didn't train on?  
* Take a look at some of the characters that have been misclassified.  Do you notice any patterns?  The network only produces the relative probabilities that the input is any of the possible characters.  Can you find examples where the network is unsure of the result?  

## Step 4: Denoising

Next we'll build and train a neural network (an autoencoder) for a different task: denoising images.

Next, create and train a convolutional autoencoder to denoise these images.  The autoencoder network will probably be similar to the one we made in class, but then you train it, the inputs should be your noisy images (original image + some guassian (normal) noise added), and the expected outputs should be the "clean" images.  It might be easiest to make a new NP array with the added noise.

Create a plot showing the noisy and denoised versions of some inputs to verify that your denoiser had the desired effect.

