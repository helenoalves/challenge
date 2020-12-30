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
