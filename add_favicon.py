import os
from pathlib import Path

# Qovluq yolu
TARGET_DIR = r"C:\Users\T.T\Desktop\GitHub\Computer-Satisi\ComputerSatisi\ComputerSatisi2\frontEnd"

# Əlavə olunacaq favicon kodu
favicon_tag = '    <link rel="icon" type="image/png" href="./Gemini_Generated_Image_vo6oi3vo6oi3vo6o.jpg">'

def add_favicon():
    path = Path(TARGET_DIR)
    if not path.exists():
        print(f"Xəta: Qovluq tapılmadı -> {TARGET_DIR}")
        return

    # Bütün HTML fayllarını tap (alt qovluqlar daxil olmaqla)
    html_files = list(path.rglob("*.html"))
    print(f"Cəmi {len(html_files)} HTML faylı tapıldı. Proses başlayır...")

    for file_path in html_files:
        try:
            with open(file_path, "r", encoding="utf-8") as f:
                content = f.read()

            # Əgər faylda bu favicon artıq mövcuddursa, keç
            if "Gemini_Generated_Image_vo6oi3vo6oi3vo6o.jpg" in content:
                print(f"Atlandı (artıq mövcuddur): {file_path.name}")
                continue

            # </head> teqindən əvvəl yerləşdir
            if "</head>" in content:
                new_content = content.replace("</head>", f"{favicon_tag}\n</head>")
            elif "<head>" in content:
                new_content = content.replace("<head>", f"<head>\n{favicon_tag}")
            else:
                # Başlıq teqi yoxdursa birbaşa yuxarı əlavə et
                new_content = f"{favicon_tag}\n" + content

            with open(file_path, "w", encoding="utf-8") as f:
                f.write(new_content)
                
            print(f"Uğurla əlavə edildi: {file_path.name}")

        except Exception as e:
            print(f"Xəta baş verdi ({file_path.name}): {e}")

    print("\nBütün əməliyyatlar tamamlandı!")

if __name__ == "__main__":
    add_favicon()