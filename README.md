# FileImport
FileImport é um sistema de processamento de dados de vendas que importa arquivos e processa os mesmos mostrando
seu número de cliente, vendedores, melhor venda e pior vendedor.

## Arquitetura
O projeto inicia em uma aplicação que verifica todos os arquivos que são colocados ou alterado na pasta raiz, <user.home>/data/in/.
Quando um arquivo é criado ou modificado na pasta raiz é disparada uma thread que inicia a verificação do arquivo conforme o padrão definido de verificações.
Os arquivos de saída do processamento são colocados na pasta <user.home>/data/out.
Ao criar ou modificar um arquivo com o nome stop.run na pasta raiz de entrada, <user.home>/data/in, o sistema para sua execução. 

## Pré-requisitos
- Editor de Código (intellij)
- Java JDk 11
- Gradle

## Bibliotecas utilizadas
- Lombok
- java.nio
- mockito
- junit

## Poderia ser melhor
Alguns pontos poderiam ser melhor explorados mas a restrição de prazo de dois dias impediu:
1. Remover os itens do pacote entities e criar um sufixo entity para ter uma divisão mais horizontal voltada a dominio e que confiasse nos sufixos.
2. Dividir as função de criação utilizadas no EntityHelper em classes separadas que pode ser importadas por pacotes.
3. Fazer testes de erros e que não fossem happy path, fazer testes de edge cases.
4. Externalizar propriedades de nomes de diretórios e arquivos e as classes de estratégia de conversão de linhas.
5. Aumentar cobertura e integrar teste de cobertura ao código

### Contato
```
Email: helenoa@gmail.com
```
