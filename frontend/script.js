async function traducir() {
    const texto = document.getElementById("texto").value;
    const idioma = document.getElementById("idioma").value;
    const tono = document.getElementById("tono").value;

    if (!texto) {
        alert("Escribe algo");
        return;
    }

    document.getElementById("resultado").innerText = "Cargando...";

    try {
        const res = await fetch("https://phonics-deferral-deserving.ngrok-free.dev/api/traducir", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'ngrok-skip-browser-warning': 'true'
            },
            body: JSON.stringify({
                texto: texto,
                idioma: idioma,
                tono: tono,
                contexto: ""
            })
        });

        if (!res.ok) {
            throw new Error("Error en el servidor: " + res.status);
        }

        const data = await res.json();

        document.getElementById("resultado").innerHTML = `
            <b>Traducción:</b> ${data.traduccion} <br><br>
            <b>Explicación:</b> ${data.explicacion} <br><br>
            <b>Alternativa:</b> ${data.alternativa} <br><br>
            <b>Confianza:</b> ${data.confianza}%
        `;

    } catch (error) {
        console.error(error);
        document.getElementById("resultado").innerText = "Error de conexión. Revisa la consola (F12).";
    }
}