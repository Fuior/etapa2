Am pachetul "core" in care am implementat functionalitatea bancii.
    -Comenzile de tip transfer bancar sunt in pachetul "core.transactions". 
    Am folosit design pattern-ul Factory pentru a le gestiona mai usor.
    -Comenzile care au legatura cu conturile, cardurile si userii sunt in pachetul "core.service" 
    si sunt implementate cu design pattern-ul Command.

Am creat pachetul "models" in care am clase ce nu contin metode sau au metode ajutatoare.

In pachetul "fileio" am pus clasele de output.

Am folosit design pattern-ul Singleton in clasa "BankRepository" pentru a ma asigura 
ca este creata o singura instanta a acestei clase.
