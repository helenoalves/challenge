# FileImport
FileImport é um sistema de processamento de dados de vendas que importa arquivos .dat e processa mostrando 
seu número de cliente, vendedores, melhor venda e pior vendedor.

## Arquitetura
O projeto inicia e ele possui um scheaduler que verifica se existe algum arquivo **.dat** na pasta  ``` nomeUsurio/data/in ``` e cria o
processamento.
O scheaduler faz o sistema rodar sempre em um loop temporal que é configuravel sempre aguardando novos dados para processar.   
O sistema foi Escrito em SpringBoot pela facilidade de iniciar o projeto e por rodar em um servidor que vem com o mesmo.

## Pré-requisitos
  - Editor de Código (intellij)
  - Java JDk 11
  - Gradle
  
## Bibliotecas utilizadas
  - Lombok
  - java.nio

### Contato
```
Email :helenoa@gmail.com
```
