#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>

// 数字栈结构
typedef struct _numstack {
    double data;
    struct _numstack *pre;
} numStack;

// 操作符栈结构
typedef struct _opstack {
    char ope;
    struct _opstack *pre;
} opStack;

// 栈顶指针及状态管理
typedef struct _head {
    numStack *numTop;   // 数字栈顶
    opStack *opTop;     // 操作符栈顶
    int isMultiDigit;   // 标记是否是多位数
} Stack;

// 函数声明
Stack* iniStack();                // 初始化栈
void pushNum(Stack *head, char a); // 数字入栈
void pushOp(Stack *head, char op); // 操作符入栈
double popNum(Stack *head);       // 数字出栈
char popOp(Stack *head);          // 操作符出栈
void count(Stack *head);          // 执行运算
int isNum(char a);                // 判断字符是否为数字
char compare(char a, char b);     // 比较操作符优先级

int main() {
    printf("请输入运算式，以等号结尾:\n");
    char input[256];
    fgets(input, sizeof(input), stdin);

    Stack *head = iniStack();
    int len = strlen(input);
    int i;     // C89 兼容写法：变量声明放在循环外
    char ch;

    for (i = 0; i < len; i++) {
        ch = input[i];
        if (ch == '=' || ch == '\n' || ch == '\0') {
            break; // 遇到等号或结束符停止处理
        }

        if (isNum(ch)) {
            pushNum(head, ch);
            head->isMultiDigit = 1; // 标记连续数字输入
        } else {
            head->isMultiDigit = 0;
            if (ch == '(') {
                pushOp(head, ch);
            } else if (ch == ')') {
                // 处理右括号：弹出直到左括号
                while (head->opTop != NULL && head->opTop->ope != '(') {
                    count(head);
                }
                if (head->opTop != NULL && head->opTop->ope == '(') {
                    popOp(head); // 弹出左括号
                } else {
                    fprintf(stderr, "错误：括号不匹配\n");
                    exit(EXIT_FAILURE);
                }
            } else {
                // 处理运算符：比较优先级并弹出高优先级运算符
                while (head->opTop != NULL && head->opTop->ope != '(' && compare(ch, head->opTop->ope) != '>') {
                    count(head);
                }
                pushOp(head, ch);
            }
        }
    }

    // 处理剩余运算符
    while (head->opTop != NULL) {
        count(head);
    }

    // 输出结果
    if (head->numTop != NULL) {
        printf("计算结果: %.3f\n", head->numTop->data);
    } else {
        printf("无有效输入\n");
    }

    return 0;
}

// 判断字符是否为数字（0-9）
int isNum(char a) {
    return (a >= '0' && a <= '9') ? 1 : 0;
}

// 初始化栈
Stack* iniStack() {
    Stack *p = (Stack*)malloc(sizeof(Stack));
    if (!p) {
        fprintf(stderr, "内存分配失败\n");
        exit(EXIT_FAILURE);
    }
    p->numTop = NULL;
    p->opTop = NULL;
    p->isMultiDigit = 0;
    return p;
}

// 数字入栈（处理多位数）
void pushNum(Stack *head, char a) {
    if (head->isMultiDigit && head->numTop != NULL) {
        // 连续数字，组合成多位数
        head->numTop->data = head->numTop->data * 10 + (a - '0');
    } else {
        // 新数字节点
        numStack *p = (numStack*)malloc(sizeof(numStack));
        if (!p) {
            fprintf(stderr, "内存分配失败\n");
            exit(EXIT_FAILURE);
        }
        p->data = a - '0';
        p->pre = head->numTop;
        head->numTop = p;
    }
}

// 操作符入栈
void pushOp(Stack *head, char op) {
    opStack *p = (opStack*)malloc(sizeof(opStack));
    if (!p) {
        fprintf(stderr, "内存分配失败\n");
        exit(EXIT_FAILURE);
    }
    p->ope = op;
    p->pre = head->opTop;
    head->opTop = p;
}

// 数字出栈
double popNum(Stack *head) {
    if (head->numTop == NULL) {
        fprintf(stderr, "数字栈为空\n");
        exit(EXIT_FAILURE);
    }
    double data = head->numTop->data;
    numStack *temp = head->numTop;
    head->numTop = head->numTop->pre;
    free(temp);
    return data;
}

// 操作符出栈
char popOp(Stack *head) {
    if (head->opTop == NULL) {
        fprintf(stderr, "操作符栈为空\n");
        exit(EXIT_FAILURE);
    }
    char op = head->opTop->ope;
    opStack *temp = head->opTop;
    head->opTop = head->opTop->pre;
    free(temp);
    return op;
}

// 比较操作符优先级
char compare(char a, char b) {
    const char operators[] = {'+', '-', '*', '/', '(', ')', '='}; // 移除 ^
    // 优先级表（7x7）
    const char priority[7][7] = {
        /* +    -    *    /    (    )    = */
        {'>', '>', '<', '<', '<', '>', '>'}, // +
        {'>', '>', '<', '<', '<', '>', '>'}, // -
        {'>', '>', '>', '>', '<', '>', '>'}, // *
        {'>', '>', '>', '>', '<', '>', '>'}, // /
        {'<', '<', '<', '<', '<', '=', ' '}, // (
        {'>', '>', '>', '>', ' ', '>', '>'}, // )
        {'<', '<', '<', '<', '<', '<', '<'}  // =
    };
    int i, j;
    for (i = 0; i < 7; i++) {
        if (operators[i] == a) break;
    }
    for (j = 0; j < 7; j++) {
        if (operators[j] == b) break;
    }
    if (i >= 7 || j >= 7) {
        fprintf(stderr, "无效操作符: %c\n", (i >= 7) ? a : b);
        exit(EXIT_FAILURE);
    }
    return priority[i][j];
}

// 执行运算
void count(Stack *head) {
    if (head->numTop == NULL || head->numTop->pre == NULL) {
        fprintf(stderr, "无效表达式\n");
        exit(EXIT_FAILURE);
    }

    char op = popOp(head);
    double b = popNum(head);
    double a = popNum(head);
    double result;

    switch(op) {
        case '+': result = a + b; break;
        case '-': result = a - b; break;
        case '*': result = a * b; break;
        case '/':
            if (b == 0) {
                fprintf(stderr, "除数不能为零\n");
                exit(EXIT_FAILURE);
            }
            result = a / b;
            break;
        default:
            fprintf(stderr, "无效操作符: %c\n", op);
            exit(EXIT_FAILURE);
    }

    // 结果压入数字栈
    numStack *p = (numStack*)malloc(sizeof(numStack));
    if (!p) {
        fprintf(stderr, "内存分配失败\n");
        exit(EXIT_FAILURE);
    }
    p->data = result;
    p->pre = head->numTop;
    head->numTop = p;
}
