(() => {
  const footerSlot = document.querySelector("[data-site-footer]");
  if (!footerSlot) return;

  const footerUrl = new URL("../html/footer.html", document.currentScript.src);

  fetch(footerUrl)
    .then((response) => {
      if (!response.ok) {
        throw new Error(`Não foi possível carregar o rodapé: ${response.status}`);
      }
      return response.text();
    })
    .then((html) => {
      footerSlot.innerHTML = html;
    })
    .catch((error) => {
      console.error("Falha ao carregar o rodapé compartilhado.", error);
    });
})();
