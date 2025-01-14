document.body.style.backgroundColor = "yellow";

// Create the h1 element
let h1 = document.createElement("h1");
h1.textContent = "Brandon Mountan's Webpage Using Only Javascript";

// Get the body element
let body = document.body;

// Append the h1 to the body
body.appendChild(h1);

let h2 = document.createElement("h2");
h2.textContent = "About Me!";
body.appendChild(h2);

let p1 = document.createElement("p");
p1.textContent = "I am a student in the MSD program at the University of Utah";
body.appendChild(p1);
