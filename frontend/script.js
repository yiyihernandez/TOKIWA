function traducir() {
  const texto = document.getElementById("texto").value;

  if (!texto) {
    alert("Escribe algo primero");
    return;
  }

  document.getElementById("resultado").innerText = "Cargando...";
}