# Ən yüngül Nginx imicini istifadə edirik
FROM nginx:alpine

# Sənin HTML, CSS, JS fayllarını Nginx-in standart qovluğuna kopyalayırıq
# Əgər kodların bir qovluğun içindədirsə (məs. 'public'), buranı './public/.' et
COPY . /usr/share/nginx/html

# 80 portunu açırıq
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]