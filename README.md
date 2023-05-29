# minhasfinancas-backend
Backend do aplicativo minhas finanças

## Projeto de controle de entrada e saída financeira com Spring Boot Java e o Spring Security para a autenticação JWT, usando docker para o servidor de banco de dados POSTGRES e pgAdmin4 para visualização das tabelas.

##Abaixo segue o link do arquivo do docker-compose.yml para você subir após ter instalado e configurado o docker em seu computador. O Arquivo pode ser colocado em qualquer lugar do seu computador e para você rodar deve executar o seguinte comando:

Arquivo docker-compose para usar no comando abaixo:

[https://github.com/bruno130187/minhasfinancas-backend/blob/master/src/docker-compose.yaml]

### `docker-compose -f c:\pasta-do-arquivo\docker-compose.yaml up`

##Assim que você executar o docker irá baixar a imagem do Postgres SQL e irá subir um servior dele no seu docker na porta 5432

## E para o pgAdmin4 você irá acessar, após subir no docker conforme informado acima, colocando a seguinte url no seu navegador:

[http://localhost:16543/]

Para se conectar usando pgAdmin pela URL acima utilize o acesso que está no arquivo focker-compose.yaml acima (email e senha informados no serviço pgadmin).

A Database já é criada pelo docker-compose, basta você rodar o script abaixo antes de iniciar a aplicação:

[https://drive.google.com/file/d/1nHiScBfj4QyPT54vJgWuE-jTQJD1tR1M/view?usp=sharing]

## Após abrir o projeto com o a sua IDEA de preferência (Eclipse, Intellij, Spring Tools, Netbeans) basta rodar a aplicação API que ficará disponível na porta http://localhost:8080.

Link do Projeto Frontend:
[https://github.com/bruno130187/minhasfinancas-frontend]

Segue meu LinkedIn: [https://www.linkedin.com/in/bruno-araujo-oficial/]
