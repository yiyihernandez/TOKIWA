/* =====================================================
   CURSOR
===================================================== */
const cur  = document.getElementById('cursor');
const ring = document.getElementById('cursor-ring');

let mx = window.innerWidth  / 2;
let my = window.innerHeight / 2;
let rx = mx, ry = my;

document.addEventListener('mousemove', e => { mx = e.clientX; my = e.clientY; });

(function loop() {
    cur.style.left  = mx + 'px';  cur.style.top  = my + 'px';
    rx += (mx - rx) * 0.12;       ry += (my - ry) * 0.12;
    ring.style.left = rx + 'px';  ring.style.top = ry + 'px';
    requestAnimationFrame(loop);
})();


/* =====================================================
   ANIMACIÓN ENTRADA HERO
===================================================== */
window.addEventListener('load', () => {
    setTimeout(() => {
        document.getElementById('hero-texto').classList.add('visible');
        document.getElementById('hero-linea').classList.add('visible');
        document.getElementById('hero-sub').classList.add('visible');
        document.getElementById('scroll-hint').classList.add('visible');
    }, 150);
});


/* =====================================================
   CAPAS PERSONAJE — HERO
===================================================== */
const hero   = document.getElementById('hero');
const p1     = document.getElementById('p1');   // PARTE1BRAZO
const p2     = document.getElementById('p2');   // PARTE2BRAZO
const mg     = document.getElementById('mg');   // MANGABRAZO
const mn     = document.getElementById('mn');   // MANOESPADA
const dragon = document.querySelector('.capa-dragon');

const MAX_ANG = 0.7;
let idleTimer, idleFrame;
let idle = false;
let lastNX = 0, lastNY = 0;


/* =====================================================
   MOUSE MOVE — brazo sigue el cursor
===================================================== */
hero.addEventListener('mousemove', e => {
    clearTimeout(idleTimer);
    if (idle) { idle = false; cancelAnimationFrame(idleFrame); }

    const r  = hero.getBoundingClientRect();
    const nx = (e.clientX - r.left) / r.width  - 0.5;
    const ny = (e.clientY - r.top)  / r.height - 0.5;

    lastNX = nx;
    lastNY = ny;

    const ang   = nx * MAX_ANG;
    const moveX = nx * 8;
    const moveY = ny * 8;

    p1.style.transform = `translate(${moveX * 0.4}px, ${moveY * 0.4}px) rotate(${ang}deg)`;
    p2.style.transform = `translate(${moveX * 0.7}px, ${moveY * 0.7}px) rotate(${ang * 0.9}deg)`;
    mg.style.transform = `translate(${moveX * 0.9}px, ${moveY * 0.9}px) rotate(${ang * 0.8}deg)`;
    mn.style.transform = `translate(${moveX}px,       ${moveY}px)       rotate(${ang * 0.7}deg)`;

    dragon.style.transform = `translate(${nx * 15}px, ${ny * 15}px)`;

    idleTimer = setTimeout(startIdle, 1200);
});


/* =====================================================
   MODO REPOSO
===================================================== */
function startIdle() {
    idle = true;
    let t = 0;

    function reposo() {
        if (!idle) return;
        t += 0.02;

        const ex    = Math.sin(t)       * 2;
        const ey    = Math.cos(t * 1.5) * 1.5;
        const moveX = (lastNX * 8) + ex;
        const moveY = (lastNY * 8) + ey;
        const ang   =  lastNX * MAX_ANG;

        p1.style.transform = `translate(${moveX * 0.4}px, ${moveY * 0.4}px) rotate(${ang}deg)`;
        p2.style.transform = `translate(${moveX * 0.7}px, ${moveY * 0.7}px) rotate(${ang * 0.9}deg)`;
        mg.style.transform = `translate(${moveX * 0.9}px, ${moveY * 0.9}px) rotate(${ang * 0.8}deg)`;
        mn.style.transform = `translate(${moveX}px,       ${moveY}px)       rotate(${ang * 0.7}deg)`;

        dragon.style.transform = `translate(${lastNX * 15 + Math.sin(t) * 6}px, ${lastNY * 15 + Math.cos(t) * 4}px)`;

        idleFrame = requestAnimationFrame(reposo);
    }
    reposo();
}


/* =====================================================
   MOUSE LEAVE HERO
===================================================== */
hero.addEventListener('mouseleave', () => {
    idle = false;
    cancelAnimationFrame(idleFrame);

    [p1, p2, mg, mn, dragon].forEach(el => el.style.transition = 'transform .8s ease');

    p1.style.transform     = 'translate(0,0) rotate(0deg)';
    p2.style.transform     = 'translate(0,0) rotate(0deg)';
    mg.style.transform     = 'translate(0,0) rotate(0deg)';
    mn.style.transform     = 'translate(0,0) rotate(0deg)';
    dragon.style.transform = 'translate(0,0)';

    setTimeout(() => {
        [p1, p2, mg, mn, dragon].forEach(el => el.style.transition = '');
    }, 800);
});


/* =====================================================
   WARRIOR — paralaje con el mouse en la sección
===================================================== */
const secTrad   = document.getElementById('seccion-traductor');
const warriorEl = document.getElementById('warrior-img');

secTrad.addEventListener('mousemove', e => {
    const r  = secTrad.getBoundingClientRect();
    const nx = (e.clientX - r.left) / r.width  - 0.5;
    const ny = (e.clientY - r.top)  / r.height - 0.5;

    warriorEl.style.transition = 'transform .12s linear, opacity 1.3s ease .2s';
    warriorEl.style.transform  = `translateX(${-nx * 20}px) translateY(${ny * 10}px)`;
});

secTrad.addEventListener('mouseleave', () => {
    warriorEl.style.transition = 'transform .8s ease, opacity 1.3s ease .2s';
    warriorEl.style.transform  = 'translateX(0) translateY(0)';
});


/* =====================================================
   SCROLL — aparece sección traductor
===================================================== */
const obs = new IntersectionObserver(entries => {
    entries.forEach(e => {
        if (e.isIntersecting) {
            secTrad.classList.add('visible');
            obs.disconnect();
        }
    });
}, { threshold: 0.08 });

obs.observe(secTrad);


/* =====================================================
   TRADUCTOR — lógica original sin cambios
===================================================== */
async function traducir() {
    const texto  = document.getElementById('texto').value;
    const idioma = document.getElementById('idioma').value;
    const tono   = document.getElementById('tono').value;

    if (!texto) { alert('Escribe algo'); return; }

    document.getElementById('resultado').innerHTML =
        '<div class="cargando">Traduciendo</div>';

    try {
        const res = await fetch('https://phonics-deferral-deserving.ngrok-free.dev/api/traducir', {
            method:  'POST',
            headers: {
                'Content-Type':               'application/json',
                'ngrok-skip-browser-warning': 'true'
            },
            body: JSON.stringify({ texto, idioma, tono, contexto: '' })
        });

        if (!res.ok) throw new Error('Error servidor: ' + res.status);

        const data = await res.json();

        document.getElementById('resultado').innerHTML = `
            <div class="resultado-bloque">
                <div class="etiqueta">Traducción</div>
                <div class="valor">${data.traduccion}</div>
            </div>
            <div class="resultado-bloque">
                <div class="etiqueta">Explicación</div>
                <div class="valor" style="font-size:13px; color:#9a8a8a">${data.explicacion}</div>
            </div>
            <div class="resultado-bloque">
                <div class="etiqueta">Alternativa</div>
                <span class="alt-tag">${data.alternativa}</span>
            </div>
            <div class="resultado-bloque">
                <div class="etiqueta">Confianza</div>
                <span class="confianza-badge">⬥ ${data.confianza}%</span>
            </div>
        `;
    } catch (err) {
        console.error(err);
        document.getElementById('resultado').innerHTML =
            '<span style="color:#e74c3c; font-size:13px; letter-spacing:1px">Error de conexión. Revisa la consola (F12).</span>';
    }
}


/* =====================================================
   BOTONES UTILIDADES
===================================================== */
function limpiar() {
    document.getElementById('texto').value = '';
    document.getElementById('resultado').innerHTML =
        '<span class="resultado-vacio">La traducción aparecerá aquí</span>';
}

function copiarTexto() {
    const t = document.getElementById('texto').value;
    if (t) navigator.clipboard.writeText(t);
}

function copiarResultado() {
    const t = document.getElementById('resultado').innerText;
    if (t) navigator.clipboard.writeText(t);
}