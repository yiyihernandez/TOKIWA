function traducir() {
  const texto = document.getElementById("texto").value;

  if (!texto) {
    alert("Escribe algo");
    return;
  }

  document.getElementById("resultado").innerText = "Cargando...";
}