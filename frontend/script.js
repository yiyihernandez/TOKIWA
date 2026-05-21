const cur = document.getElementById('cursor');
const ring = document.getElementById('cursor-ring');

let mx = window.innerWidth / 2;
let my = window.innerHeight / 2;

let rx = mx;
let ry = my;

document.addEventListener('mousemove', e => {
    mx = e.clientX;
    my = e.clientY;
});

(function loop(){

    cur.style.left = mx+'px';
    cur.style.top  = my+'px';

    rx += (mx-rx)*0.12;
    ry += (my-ry)*0.12;

    ring.style.left=rx+'px';
    ring.style.top =ry+'px';

    requestAnimationFrame(loop);

})();


window.addEventListener('load',()=>{
setTimeout(()=>{
document
.getElementById('hero-texto')
.classList.add('visible');

document
.getElementById('hero-linea')
.classList.add('visible');

document
.getElementById('hero-sub')
.classList.add('visible');

document
.getElementById('scroll-hint')
.classList.add('visible');

},150);

});



/*CAPAS*/

const hero=document.getElementById('hero');

const p1=document.getElementById('p1');
const p2=document.getElementById('p2');
const mg=document.getElementById('mg');
const mn=document.getElementById('mn');

const dragon=
document.querySelector('.capa-dragon');



const MAX_ANG=.7;

let idle=false;

let idleFrame;

let idleTimer;

let lastNX=0;
let lastNY=0;


hero.addEventListener('mousemove',e=>{
clearTimeout(idleTimer);
if(idle){
idle=false;
cancelAnimationFrame(
idleFrame
);

}


const r=
hero.getBoundingClientRect();

const nx=
(e.clientX-r.left)
/r.width-.5;

const ny=
(e.clientY-r.top)
/r.height-.5;

lastNX=nx;
lastNY=ny;


const ang=
nx*MAX_ANG;


const moveX=
nx*8;

const moveY=
ny*8;


p1.style.transform=`translate(${moveX*.4}px,${moveY*.4}px)rotate(${ang}deg)`;
p2.style.transform=`translate(${moveX*.7}px,${moveY*.7}px)rotate(${ang*.9}deg)`;


mg.style.transform=`translate(${moveX*.9}px,${moveY*.9}px)rotate(${ang*.8}deg)`;


mn.style.transform=`translate(${moveX}px,${moveY}px)rotate(${ang*.7}deg)`;



dragon.style.transform=`translate(${nx*15}px,${ny*15}px)`;

idleTimer=
setTimeout(
startIdle,
1200
);

});





/*REPOSO SUAVE*/

function startIdle(){
idle=true;
let t=0;

function reposo(){
if(!idle)return;

t+=0.02;

/* movimiento flotante */
const ex=
Math.sin(t)*2;
const ey=
Math.cos(t*1.5)*1.5;


const moveX=
(lastNX*8)+ex;

const moveY=
(lastNY*8)+ey;

const ang=
lastNX*MAX_ANG;

/* posicion actual */
p1.style.transform=`translate(${moveX*.4}px,${moveY*.4}px)rotate(${ang}deg)`;
p2.style.transform=`translate(${moveX*.7}px,${moveY*.7}px)rotate(${ang*.9}deg)`;
mg.style.transform=`translate(${moveX*.9}px,${moveY*.9}px)rotate(${ang*.8}deg)`;
mn.style.transform=`translate(${moveX}px,${moveY}px)rotate(${ang*.7}deg)`;
dragon.style.transform=`translate(${lastNX*15 + Math.sin(t)*6}px,${lastNY*15 + Math.cos(t)*4}px)`;

idleFrame=requestAnimationFrame(reposo);
}

reposo();

}


hero.addEventListener(
'mouseleave',
()=>{

idle=false;
cancelAnimationFrame(
idleFrame
);

[p1,p2,mg,mn,dragon]

.forEach(el=>{

el.style.transition=
'transform .8s ease';

});



p1.style.transform=
'translate(0,0) rotate(0deg)';

p2.style.transform=
'translate(0,0) rotate(0deg)';

mg.style.transform=
'translate(0,0) rotate(0deg)';

mn.style.transform=
'translate(0,0) rotate(0deg)';


dragon.style.transform=
'translate(0,0)';



setTimeout(()=>{

[p1,p2,mg,mn,dragon]

.forEach(el=>{

el.style.transition='';

});

},800);


});





/*APARECER TRADUCTOR*/

const obs=
new IntersectionObserver(entries=>{

entries.forEach(e=>{

if(e.isIntersecting){

document
.getElementById(
'seccion-traductor'
)
.classList.add(
'visible'
);

obs.disconnect();

}

});

},

{
threshold:.08
});


obs.observe(

document
.getElementById(
'seccion-traductor'
)

);





/*TRADUCTOR*/

async function traducir(){

const texto=document.getElementById('texto').value;

const idioma=document.getElementById('idioma').value;

const tono=document.getElementById('tono').value;

if(!texto){
alert('Escribe algo');
return;

}


document
.getElementById(
'resultado'
)

.innerHTML=
'<div class="cargando">Traduciendo</div>';

try{

const res=
await fetch(
'https://phonics-deferral-deserving.ngrok-free.dev/api/traducir',
{
method:'POST',

headers:{
'Content-Type':'application/json',
'ngrok-skip-browser-warning':'true'
},

body:JSON.stringify({texto,idioma,tono,contexto:''})

});

if(!res.ok)
throw new Error(
'Error servidor'
);


const data=
await res.json();


document
.getElementById(
'resultado'
)

.innerHTML=`<div class="resultado-bloque"><div class="etiqueta">Traducción</div><div class="valor">${data.traduccion}</div></div>`;

}catch(err){

console.error(err);

}

}