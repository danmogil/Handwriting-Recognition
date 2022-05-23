# Handwriting Recognition
## Background -
[*Artificial Neural Networks (ANNs)*](https://en.wikipedia.org/wiki/Artificial_neural_network) propagate inputs through a network of connections, simulating a neural "spike train." Artificial Neurons process inputs, yielding the neuron's *action potential* <sup>*</sup>. These values propagate through the network until reaching the final (output) layer. Each node in the output layer represents an output value. The output node with the greatest action potential is the most likely result. Training the network involves [backpropagation](https://en.wikipedia.org/wiki/Backpropagation): the finetuning of action-potential calculations by manipulating *weights*, a property unique to each neuronal connection.
| ![ANN](https://user-images.githubusercontent.com/84862652/169749701-0825f004-4fd5-4156-a959-05ba0d51f5ea.png) | <img src="https://user-images.githubusercontent.com/84862652/169749817-0761a8e3-f1dc-428a-8767-6a2cf77e7e27.png" height="250"></img> |
| :--: | :--: |
| *Multilayered ANN Highlighting Neuronal Connections* | *Inputs → Activation Function → Action Potential* |

<sub>*Biologically, an action potential is a threshold that determines whether a neighboring neuron fires. Artificially, action potential represents a neuron's strength.</sub>
## Preview -
![GUI](./PREVIEW.PNG "GUI preview")
<br>
No external libraries, training/test data sourced from the [MNIST database](http://yann.lecun.com/exdb/mnist/).
