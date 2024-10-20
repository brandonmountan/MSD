// Function to find the index of the minimum element
function findMinLocation(array, start) {
  let minIndex = start;
  for (let i = start + 1; i < array.length; i++) {
    if (array[i] < array[minIndex]) {
      minIndex = i;
    }
  }
  return minIndex;
}

// Selection Sort Function
function selectionSort(array, compare = (a, b) => a < b) {
  for (let i = 0; i < array.length; i++) {
    const minIndex = findMinLocation(array, i);
    if (minIndex !== i) {
      // Swap elements
      [array[i], array[minIndex]] = [array[minIndex], array[i]];
    }
  }
}

// Test Sorting Function
function testSorting() {
  const numbers = [64, 25, 12, 22, 11];
  selectionSort(numbers);
  console.log('Sorted Numbers:', numbers);

  const floats = [64.5, 25.3, 12.1, 22.4, 11.0];
  selectionSort(floats);
  console.log('Sorted Floats:', floats);

  const strings = ['banana', 'Apple', 'cherry', 'date'];
  selectionSort(strings);
  console.log('Sorted Strings:', strings);

  const mixed = [3, 'banana', 1.5, 'Apple', 2];
  selectionSort(mixed, (a, b) => String(a).toLowerCase() < String(b).toLowerCase());
  console.log('Sorted Mixed:', mixed);

  // Sorting people
  const people = [
    { first: 'Jerry', last: 'Seinfeld' },
    { first: 'George', last: 'Costanza' },
    { first: 'Elaine', last: 'Bennis' },
    { first: 'Cosmo', last: 'Kramer' }
  ];
  selectionSort(people, (a, b) => {
    if (a.last === b.last) {
      return a.first < b.first;
    }
    return a.last < b.last;
  });
  console.log('Sorted People by Last Name:', people);
}

// Run the test function
testSorting();
