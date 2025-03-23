#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>

// ����ջ�ṹ
typedef struct _numstack {
    double data;
    struct _numstack *pre;
} numStack;

// ������ջ�ṹ
typedef struct _opstack {
    char ope;
    struct _opstack *pre;
} opStack;

// ջ��ָ�뼰״̬����
typedef struct _head {
    numStack *numTop;   // ����ջ��
    opStack *opTop;     // ������ջ��
    int isMultiDigit;   // ����Ƿ��Ƕ�λ��
} Stack;

// ��������
Stack* iniStack();                // ��ʼ��ջ
void pushNum(Stack *head, char a); // ������ջ
void pushOp(Stack *head, char op); // ��������ջ
double popNum(Stack *head);       // ���ֳ�ջ
char popOp(Stack *head);          // ��������ջ
void count(Stack *head);          // ִ������
int isNum(char a);                // �ж��ַ��Ƿ�Ϊ����
char compare(char a, char b);     // �Ƚϲ��������ȼ�

int main() {
    printf("����������ʽ���ԵȺŽ�β:\n");
    char input[256];
    fgets(input, sizeof(input), stdin);

    Stack *head = iniStack();
    int len = strlen(input);
    int i;     // C89 ����д����������������ѭ����
    char ch;

    for (i = 0; i < len; i++) {
        ch = input[i];
        if (ch == '=' || ch == '\n' || ch == '\0') {
            break; // �����ȺŻ������ֹͣ����
        }

        if (isNum(ch)) {
            pushNum(head, ch);
            head->isMultiDigit = 1; // ���������������
        } else {
            head->isMultiDigit = 0;
            if (ch == '(') {
                pushOp(head, ch);
            } else if (ch == ')') {
                // ���������ţ�����ֱ��������
                while (head->opTop != NULL && head->opTop->ope != '(') {
                    count(head);
                }
                if (head->opTop != NULL && head->opTop->ope == '(') {
                    popOp(head); // ����������
                } else {
                    fprintf(stderr, "�������Ų�ƥ��\n");
                    exit(EXIT_FAILURE);
                }
            } else {
                // ������������Ƚ����ȼ������������ȼ������
                while (head->opTop != NULL && head->opTop->ope != '(' && compare(ch, head->opTop->ope) != '>') {
                    count(head);
                }
                pushOp(head, ch);
            }
        }
    }

    // ����ʣ�������
    while (head->opTop != NULL) {
        count(head);
    }

    // ������
    if (head->numTop != NULL) {
        printf("������: %.3f\n", head->numTop->data);
    } else {
        printf("����Ч����\n");
    }

    return 0;
}

// �ж��ַ��Ƿ�Ϊ���֣�0-9��
int isNum(char a) {
    return (a >= '0' && a <= '9') ? 1 : 0;
}

// ��ʼ��ջ
Stack* iniStack() {
    Stack *p = (Stack*)malloc(sizeof(Stack));
    if (!p) {
        fprintf(stderr, "�ڴ����ʧ��\n");
        exit(EXIT_FAILURE);
    }
    p->numTop = NULL;
    p->opTop = NULL;
    p->isMultiDigit = 0;
    return p;
}

// ������ջ�������λ����
void pushNum(Stack *head, char a) {
    if (head->isMultiDigit && head->numTop != NULL) {
        // �������֣���ϳɶ�λ��
        head->numTop->data = head->numTop->data * 10 + (a - '0');
    } else {
        // �����ֽڵ�
        numStack *p = (numStack*)malloc(sizeof(numStack));
        if (!p) {
            fprintf(stderr, "�ڴ����ʧ��\n");
            exit(EXIT_FAILURE);
        }
        p->data = a - '0';
        p->pre = head->numTop;
        head->numTop = p;
    }
}

// ��������ջ
void pushOp(Stack *head, char op) {
    opStack *p = (opStack*)malloc(sizeof(opStack));
    if (!p) {
        fprintf(stderr, "�ڴ����ʧ��\n");
        exit(EXIT_FAILURE);
    }
    p->ope = op;
    p->pre = head->opTop;
    head->opTop = p;
}

// ���ֳ�ջ
double popNum(Stack *head) {
    if (head->numTop == NULL) {
        fprintf(stderr, "����ջΪ��\n");
        exit(EXIT_FAILURE);
    }
    double data = head->numTop->data;
    numStack *temp = head->numTop;
    head->numTop = head->numTop->pre;
    free(temp);
    return data;
}

// ��������ջ
char popOp(Stack *head) {
    if (head->opTop == NULL) {
        fprintf(stderr, "������ջΪ��\n");
        exit(EXIT_FAILURE);
    }
    char op = head->opTop->ope;
    opStack *temp = head->opTop;
    head->opTop = head->opTop->pre;
    free(temp);
    return op;
}

// �Ƚϲ��������ȼ�
char compare(char a, char b) {
    const char operators[] = {'+', '-', '*', '/', '(', ')', '='}; // �Ƴ� ^
    // ���ȼ���7x7��
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
        fprintf(stderr, "��Ч������: %c\n", (i >= 7) ? a : b);
        exit(EXIT_FAILURE);
    }
    return priority[i][j];
}

// ִ������
void count(Stack *head) {
    if (head->numTop == NULL || head->numTop->pre == NULL) {
        fprintf(stderr, "��Ч���ʽ\n");
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
                fprintf(stderr, "��������Ϊ��\n");
                exit(EXIT_FAILURE);
            }
            result = a / b;
            break;
        default:
            fprintf(stderr, "��Ч������: %c\n", op);
            exit(EXIT_FAILURE);
    }

    // ���ѹ������ջ
    numStack *p = (numStack*)malloc(sizeof(numStack));
    if (!p) {
        fprintf(stderr, "�ڴ����ʧ��\n");
        exit(EXIT_FAILURE);
    }
    p->data = result;
    p->pre = head->numTop;
    head->numTop = p;
}
