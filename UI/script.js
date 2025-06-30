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
  mp4: ["mp3"],
};

const fileInput = document.getElementById("fileInput");
const outputFormat = document.getElementById("outputFormat");
const convertBtn = document.getElementById("convertBtn");
const imagePreview = document.getElementById("imagePreview");
const textPreview = document.getElementById("textPreview");
const pdfPreview = document.getElementById("pdfPreview");
const htmlPreview = document.getElementById("htmlPreview");
const previewContent = document.getElementById("previewContent");
const noPreviewText = previewContent.querySelector(".no-preview");
const previewContainer = document.querySelector(".preview-container");
const appContainer = document.querySelector(".app-container");
const baseUrl = 'http://localhost:9000';

function formatJSON(jsonString) {
  try {
    const obj = JSON.parse(jsonString);
    return JSON.stringify(obj, null, 2);
  } catch (e) {
    return jsonString;
  }
}

function formatXML(xmlString) {
  try {
    const parser = new DOMParser();
    const xmlDoc = parser.parseFromString(xmlString, "text/xml");
    const serializer = new XMLSerializer();
    let formatted = '';
    let indent = '';

    function format(node, level) {
      if (node.nodeType === 3) { // Text node
        const text = node.textContent.trim();
        if (text) {
          formatted += indent + text + '\n';
        }
      } else if (node.nodeType === 1) { // Element node
        formatted += indent + '<' + node.nodeName;
        
        // Add attributes
        Array.from(node.attributes).forEach(attr => {
          formatted += ' ' + attr.name + '="' + attr.value + '"';
        });

        if (node.childNodes.length === 0) {
          formatted += '/>\n';
        } else {
          formatted += '>\n';
          indent += '  ';
          Array.from(node.childNodes).forEach(child => format(child, level + 1));
          indent = indent.slice(0, -2);
          formatted += indent + '</' + node.nodeName + '>\n';
        }
      }
    }

    format(xmlDoc.documentElement, 0);
    return formatted;
  } catch (e) {
    return xmlString;
  }
}

function formatCSV(csvString) {
  try {
    // Split into lines and filter out empty lines
    const lines = csvString.split('\n').filter(line => line.trim());
    if (lines.length === 0) return csvString;

    // Format each line
    return lines.map(line => {
      // Split by comma, but respect quotes
      const columns = line.match(/(".*?"|[^",\s]+)(?=\s*,|\s*$)/g) || [line];
      return columns.join(',  ');
    }).join('\n');
  } catch (e) {
    return csvString;
  }
}

function resetPreviewElements() {
  // Clean up any existing object URLs
  if (pdfPreview.src) URL.revokeObjectURL(pdfPreview.src);
  if (htmlPreview.src) URL.revokeObjectURL(htmlPreview.src);
  
  // Hide all preview elements
  imagePreview.style.display = "none";
  textPreview.style.display = "none";
  pdfPreview.style.display = "none";
  htmlPreview.style.display = "none";
  noPreviewText.style.display = "block";
}

function hidePreview() {
  previewContainer.classList.remove('visible');
  appContainer.classList.remove('with-preview');
  resetPreviewElements();
}

function showPreview(file) {
  // Show preview container and update layout
  previewContainer.classList.add('visible');
  appContainer.classList.add('with-preview');

  // Reset preview elements but keep container visible
  resetPreviewElements();

  // Handle different file types
  if (file.type.startsWith("image/")) {
    const reader = new FileReader();
    reader.onload = function(e) {
      imagePreview.src = e.target.result;
      imagePreview.style.display = "block";
      noPreviewText.style.display = "none";
    };
    reader.readAsDataURL(file);
  } else if (file.type === "application/json" || file.name.endsWith('.json')) {
    const reader = new FileReader();
    reader.onload = function(e) {
      textPreview.textContent = formatJSON(e.target.result);
      textPreview.style.display = "block";
      noPreviewText.style.display = "none";
    };
    reader.readAsText(file);
  } else if (file.type === "text/csv" || file.name.endsWith('.csv')) {
    const reader = new FileReader();
    reader.onload = function(e) {
      textPreview.textContent = formatCSV(e.target.result);
      textPreview.style.display = "block";
      noPreviewText.style.display = "none";
    };
    reader.readAsText(file);
  } else if (file.type === "application/xml" || file.name.endsWith('.xml')) {
    const reader = new FileReader();
    reader.onload = function(e) {
      textPreview.textContent = formatXML(e.target.result);
      textPreview.style.display = "block";
      noPreviewText.style.display = "none";
    };
    reader.readAsText(file);
  } else if (file.type === "application/pdf" || file.name.endsWith('.pdf')) {
    // Create object URL for PDF preview
    const objectUrl = URL.createObjectURL(file);
    pdfPreview.src = objectUrl;
    pdfPreview.style.display = "block";
    noPreviewText.style.display = "none";
  } else if (file.type === "text/html" || file.name.endsWith('.html')) {
    const reader = new FileReader();
    reader.onload = function(e) {
      // Create a blob with proper HTML mime type
      const htmlBlob = new Blob([e.target.result], { type: 'text/html' });
      const objectUrl = URL.createObjectURL(htmlBlob);
      htmlPreview.src = objectUrl;
      htmlPreview.style.display = "block";
      noPreviewText.style.display = "none";
    };
    reader.readAsText(file);
  } else {
    noPreviewText.textContent = `File selected: ${file.name}\nPreview not available for this file type.`;
  }
}

fileInput.addEventListener("change", () => {
  const file = fileInput.files[0];
  if (!file) {
    hidePreview();
    outputFormat.innerHTML = `<option value="">Select output format</option>`;
    outputFormat.disabled = true;
    convertBtn.disabled = true;
    return;
  }

  // Show preview
  showPreview(file);

  const ext = file.name.split('.').pop().toLowerCase();
  const destinations = formatMap[ext];

  outputFormat.innerHTML = `<option value="">Select output format</option>`;
  convertBtn.disabled = true;

  if (destinations) {
    destinations.forEach(format => {
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

outputFormat.addEventListener("change", () => {
  convertBtn.disabled = outputFormat.value === "";
});

document.getElementById('convertBtn').addEventListener('click', async function (e) {
  e.preventDefault();

  const file = fileInput.files[0];
  if (!file) {
    alert('Please select a file.');
    return;
  }
  const fileFormat = file.name.split('.').pop().toLowerCase();
  const conversionFormat = outputFormat.value;

  if (!conversionFormat) {
    alert('Please select a conversion format.');
    return;
  }

  let endpoint = '';
  const formData = new FormData();

  // Handle image conversions with /images/convert endpoint
  if (["jpg", "jpeg", "png"].includes(fileFormat)) {
    endpoint = `${baseUrl}/images/convert`;
    formData.append('file', file);
    formData.append('targetFormat', conversionFormat);
  } else {
    endpoint = `${baseUrl}/${fileFormat}-to-${conversionFormat}`;
    formData.append('file', file);
  }

  try {
    const response = await fetch(endpoint, {
      method: 'POST',
      body: formData
    });
    if (!response.ok) throw new Error('Conversion failed');
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `converted.${conversionFormat}`;
    document.body.appendChild(a);
    a.click();
    a.remove();
    window.URL.revokeObjectURL(url);
  } catch (err) {
    console.log('Error: ' + err.message);
  }
}); 