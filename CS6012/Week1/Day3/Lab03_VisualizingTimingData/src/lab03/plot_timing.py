import matplotlib.pyplot as plt

X = 0
Y = 1
def main():
    data = readFile("data.csv")
    plt.figure(1) 
    for key in data: 
        plt.plot(data[key][X], data[key][Y])
    plt.savefig('python_example1.png')
    plt.show()
    plt.close()
    


def readFile(filename):
    data = None
    delim = ","
    plot = {}
    with open(filename, 'r') as f:
        data = [line.split(delim) for line in f]
    for datum in data[1:]:
        key = datum[0]
        if(key not in plot):
            plot[key] = ([],[])
        plot[key][X].append(datum[1])
        plot[key][Y].append(datum[2])
    return plot

        
if __name__ == '__main__':
    main()
