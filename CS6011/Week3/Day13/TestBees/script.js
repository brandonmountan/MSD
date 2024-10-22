"use strict";

let thomas = { 'x': 100, 'y': 150, 'speed': Math.floor( Math.random()/2+.5 ) };
thomas.img = new Image();
thomas.img.src = "tsticker.png";

let brandon = { 'x': Math.random()*10+10, 'y': 0, 'speed': Math.floor( Math.random()*2+3 ) }
brandon.img = new Image();
brandon.img.src = "bsticker.png";

let can = document.getElementById( 'mycanvas' );
let context = can.getContext( '2d' );

context.drawImage( thomas.img, thomas.x, thomas.y, 40, 50 );
context.drawImage( brandon.img, brandon.x, brandon.y, 40, 50 );

// can.style.cursor = "none";

let cursorLocation = { x: 0, y: 0 };

function handleMouseMove( e ) {
    thomas.x = e.x;
    thomas.y = e.y;
    cursorLocation.x = e.x;
    cursorLocation.y = e.y;
}

function handleBrandonMoveX() {
    if ( Math.abs(brandon.x - cursorLocation.x ) < brandon.speed ) {
        brandon.x = cursorLocation.x;
    }
    else if ( brandon.x < cursorLocation.x ) {
        brandon.x += brandon.speed
    } else {
        brandon.x -= brandon.speed
    }
}

function handleBrandonMoveY() {
    if ( Math.abs( brandon.y - cursorLocation.y ) < brandon.speed ) {
        brandon.y = cursorLocation.y
    } else if ( brandon.y < cursorLocation.y ) {
        brandon.y += brandon.speed
    } else {
        brandon.y -= brandon.speed
    }
}


function draw() {
    context.clearRect( 0, 0, 1000, 1000 );
    context.drawImage( thomas.img, thomas.x, thomas.y, 40, 50 );
    context.drawImage( brandon.img, brandon.x, brandon.y, 30, 50 );
    context.drawImage( brandon.img, brandon.x, brandon.y, 30, 50 );
    context.drawImage( brandon.img, brandon.x, brandon.y, 30, 50 );
    context.drawImage( brandon.img, brandon.x, brandon.y, 30, 50 );

}

function mainGameLoop() {
    handleBrandonMoveX();
    handleBrandonMoveY();
    draw();
    window.requestAnimationFrame( mainGameLoop );
}

mainGameLoop();

can.onmousemove = handleMouseMove;

console.log("brandon speed: " + brandon.speed);