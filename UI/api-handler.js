const baseUrl = 'https://50rsnq0nb2.execute-api.ap-south-1.amazonaws.com';

async function convert(input, conversionFormat, inputType = 'file') {
  const file = input;
  const fileFormat = inputType === 'file' ? file.name.split('.').pop().toLowerCase() : 'url';
  let endpoint = getEndpoint(fileFormat, conversionFormat);
  let requestOptions = {};

  if (inputType === 'file') {
    const formData = new FormData();
    formData.append('file', file);
    requestOptions = {method: 'POST', body: formData};
  }
  else {
    endpoint = endpoint + input;
    requestOptions = {method: 'POST'};
  }

  try {
    const response = await fetch(endpoint, requestOptions);
    
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`Conversion failed: ${response.status} - ${errorText}`);
    }

    const blob = await response.blob();
    downloadFile(blob, conversionFormat);

    return { success: true, size: blob.size };
  } catch (err) {
    console.error('file conversion error: ', err);
    alert(`${inputTypeText} conversion failed: ${err.message}`);
    return { success: false, error: err.message };
  }
}

function downloadFile(blob, format) {
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = '';
  document.body.appendChild(a);
  a.click();
  a.remove();
  window.URL.revokeObjectURL(url);
}

function getEndpoint(fileFormat, conversionFormat){
    const imageFormats = ["jpg", "jpeg", "png"];
    const textFormats = ["json", "xml", "csv", "html"];
    const documentFormats = ["pdf", "docx"];
    const urlConversionMap = {
      qrcode: "image/url-to-qrcode",
      pdf: "document/url-to-pdf",
      html: "text/url-to-html"
    };

    if (fileFormat === "url" && urlConversionMap[conversionFormat]) {
      return `${baseUrl}/${urlConversionMap[conversionFormat]}?url=`;
    } else if (imageFormats.includes(conversionFormat)) {
        return `${baseUrl}/image/convert?targetFormat=${conversionFormat}`;
    } else if (textFormats.includes(conversionFormat)) {
        return `${baseUrl}/text/${fileFormat}-to-${conversionFormat}`;
    } else if (conversionFormat === "qrcode") {
        return `${baseUrl}/image/${fileFormat}-to-${conversionFormat}`;
    } else if (documentFormats.includes(conversionFormat)) {
        return `${baseUrl}/document/${fileFormat}-to-${conversionFormat}`;
    } else {
        return `${baseUrl}/${fileFormat}-to-${conversionFormat}`;
    }
}

window.convert = convert;
