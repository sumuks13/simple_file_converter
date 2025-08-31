const formatMap = {
  pdf: ["docx"],
  docx: ["pdf"],
  json: ["csv", "xml"],
  csv: ["json", "xml"],
  xml: ["json", "csv"],
  html: ["pdf", "docx"],
  jpg: ["png", "jpeg"],
  jpeg: ["png", "jpg"],
  png: ["jpg", "jpeg"],
  //mp4: ["mp3"],
};

// DOM elements
const inputType = document.getElementById("inputType");
const fileInput = document.getElementById("fileInput");
const urlInput = document.getElementById("urlInput");
const outputFormat = document.getElementById("outputFormat");
const convertBtn = document.getElementById("convertBtn");
const appContainer = document.querySelector(".app-container");
const fileInputSection = document.getElementById("fileInputSection");
const urlInputSection = document.getElementById("urlInputSection");
const infoIcon = document.querySelector(".info-icon");
const tooltip = document.querySelector(".supported-conversions-tooltip");
const closeBtn = tooltip ? tooltip.querySelector('.tooltip-close') : null;

// Simple input type change handler
inputType.addEventListener("change", () => {
  const selectedType = inputType.value;

  if (selectedType === "file") {
    fileInputSection.style.display = "block";
    urlInputSection.style.display = "none";
    fileInput.value = "";
  } else {
    fileInputSection.style.display = "none";
    urlInputSection.style.display = "block";
    urlInput.value = "";
  }

  resetOutputFormat();
  updateConvertButton();
});

// Event listeners
fileInput.addEventListener("change", () => {
  const file = fileInput.files[0];
  if (!file) {
    outputFormat.innerHTML = `<option value="">Select output format</option>`;
    outputFormat.disabled = true;
    convertBtn.disabled = true;
    return;
  }

  const ext = file.name.split(".").pop().toLowerCase();
  const destinations = formatMap[ext];

  outputFormat.innerHTML = `<option value="">Select output format</option>`;
  convertBtn.disabled = true;

  if (destinations) {
    destinations.forEach((format) => {
      const opt = document.createElement("option");
      opt.value = format;
      opt.textContent = format.toUpperCase();
      outputFormat.appendChild(opt);
    });
    outputFormat.disabled = false;
  } else {
    outputFormat.disabled = true;
  }
});

// URL input event listener
urlInput.addEventListener("input", () => {
  const url = urlInput.value.trim();
  if (!url) {
    outputFormat.innerHTML = `<option value="">Select output format</option>`;
    outputFormat.disabled = true;
    convertBtn.disabled = true;
    return;
  }

  //URLs only support html, pdf, and qrcode conversions
  outputFormat.innerHTML = `
    <option value="">Select output format</option>
    <option value="html">HTML</option>
    <option value="pdf">PDF</option>
    <option value="qrcode">QR Code</option>
  `;
  outputFormat.disabled = false;
});

outputFormat.addEventListener("change", () => {
  convertBtn.disabled = outputFormat.value === "";
});

convertBtn.addEventListener("click", async function (e) {
  e.preventDefault();

  const currentInputType = inputType.value;

  if (currentInputType === "file") {
    const file = fileInput.files[0];
    if (!file) {
      alert("Please select a file.");
      return;
    }

    const conversionFormat = outputFormat.value;
    if (!conversionFormat) {
      alert("Please select a conversion format.");
      return;
    }

    // Disable button during conversion
    convertBtn.disabled = true;
    convertBtn.textContent = "Converting...";

    try {
      const result = await convert(file, conversionFormat, "file");
      if (result.success) {
        console.log(`File converted successfully! Size: ${result.size} bytes`);
      }
    } catch (error) {
      console.error("Conversion failed:", error);
    } finally {
      // Re-enable button
      convertBtn.disabled = false;
      convertBtn.textContent = "Convert";
    }
  } else {
    // URL conversion
    const url = urlInput.value.trim();
    if (!url) {
      alert("Please enter a URL.");
      return;
    }

    const conversionFormat = outputFormat.value;
    if (!conversionFormat) {
      alert("Please select a conversion format.");
      return;
    }

    // Disable button during conversion
    convertBtn.disabled = true;
    convertBtn.textContent = "Converting...";

    try {
      const result = await convert(url, conversionFormat, "url");
      if (result.success) {
        console.log(`URL converted successfully! Size: ${result.size} bytes`);
      }
    } catch (error) {
      console.error("Conversion failed:", error);
    } finally {
      // Re-enable button
      convertBtn.disabled = false;
      convertBtn.textContent = "Convert";
    }
  }
});

function resetOutputFormat() {
  outputFormat.innerHTML = `<option value="">Select output format</option>`;
  outputFormat.disabled = true;
  convertBtn.disabled = true;
}

// Function to update convert button state
function updateConvertButton() {
  const currentInputType = inputType.value;

  if (currentInputType === "file") {
    const file = fileInput.files[0];
    const format = outputFormat.value;
    convertBtn.disabled = !file || !format;
  } else {
    const url = urlInput.value.trim();
    const format = outputFormat.value;
    convertBtn.disabled = !url || !format;
  }
}

// Tooltip function
function showTooltip() {
  tooltip.style.display = "flex";
  setTimeout(() => {
    document.addEventListener("mousedown", handleOutsideClick);
    document.addEventListener("keydown", handleEscape);
  }, 0);
}

function hideTooltip() {
  tooltip.style.display = "none";
  document.removeEventListener("mousedown", handleOutsideClick);
  document.removeEventListener("keydown", handleEscape);
}

function handleOutsideClick(e) {
  if (!tooltip.querySelector(".tooltip-content").contains(e.target)) {
    hideTooltip();
  }
}

function handleEscape(e) {
  if (e.key === "Escape") hideTooltip();
}

if (infoIcon && tooltip) {
  infoIcon.addEventListener("click", (e) => {
    e.stopPropagation();
    showTooltip();
  });

  tooltip.addEventListener("click", (e) => {
    if (e.target === tooltip) hideTooltip();
  });
  if (closeBtn) {
    closeBtn.addEventListener('click', hideTooltip);
  }
}
