# simple-file-converter
A microservices-based java spring boot application for converting files to different formats.

## AWS Serverless Backend Architecture

<img width="874" height="551" alt="image" src="https://github.com/user-attachments/assets/9803bf12-54ec-4d7b-9165-034e2f6d8c37" />

## Supported File Conversions

| Input Format | Output Formats         | Coverage  | Limitations                                              |
|--------------|------------------------|-----------|----------------------------------------------------------|
| JSON         | CSV, XML               | Full      |                                                          |
| CSV          | JSON, XML              | Full      |                                                          |
| XML          | JSON, CSV              | Moderate  | Complex attributes may be simplified                     |
| HTML         | PDF, DOCX              | Moderate  | Dynamic content not supported                            |
| JPG          | PNG, JPEG              | Full      |                                                          |
| JPEG         | PNG, JPG               | Full      |                                                          |
| PNG          | JPG, JPEG              | Full      |                                                          |
| PDF          | DOCX                   | Limited   | Only first 3 pages are converted. Limited font support   |
| DOCX         | PDF                    | Limited   | Limited font support                                     |
| URL          | HTML, PDF, QR Code     | Moderate  | Depends on website accessibility and content             |
