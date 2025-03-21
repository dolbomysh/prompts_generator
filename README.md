# Генератор промтов по шаблону <br>

Программа позволяет создавать серии запросов к модели FLAN-T5 по шаблонной строке и нескольким наборам аргументов. <br>

Ввод-вывод в стандартный поток. <br>

### *Формат входных данных:*
На вход подается <br>
1) шаблонная строка, она представляет собой текст(одна или несколько строк) c пропусками для аргументров вида [arg] <br>
2) пустая строка
3) количество запросов
4) каждая следующая строка - набор аргументов для запроса вида <br>
arg1 = argname1, arg2 = argname2, ...

### *Формат выходных данных:*
Для каждого запроса строка с идентификатором запроса и ответом.

### *Пример использования:*
##### Ввод: <br>
&ensp;*tell me some interesting fact about [animal] in [country]* <br>

 &ensp;*3* <br>
&ensp;*animal=cats, country=Russia* <br>
&ensp;*animal=dogs, country=Germany* <br>
&ensp;*animal=elephants, country=China* <br>

##### Вывод:<br>
&ensp;*1 : Russian cats are a member of the domestic cat family.* <br>
&ensp;*2 : German Shepherds are a breed of dog.* <br>
&ensp;*3 : In China, elephants are a major part of the zoo exhibit.* <br><br>



Возможно создание шаблонных строк (в том числе, с рекурсивной подстановкой с помощью класса TemplatedString и получение ответов на запросы функцией generatePrompts.
