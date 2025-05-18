/**
 * 云文档平台UI组件系统
 * 提供统一的UI组件和交互处理
 */

// 通用UI控制器
const UIController = {
    // 显示加载指示器
    showLoading: function() {
        document.getElementById('loading').classList.remove('hidden');
    },

    // 隐藏加载指示器
    hideLoading: function() {
        console.log("Hiding loading indicator");
        const loadingElement = document.getElementById('loading');
        if (loadingElement) {
            loadingElement.classList.add('hidden');
        } else {
            console.error("Loading element not found");
        }
    },

    // 显示指定页面，隐藏其他页面
    showPage: function(pageId) {
        console.log("33333");
        // 隐藏所有页面
        const pages = document.querySelectorAll('.flex.h-screen');
        pages.forEach(page => page.classList.add('hidden'));

        // 显示指定页面
        const targetPage = document.getElementById(pageId);
        if (targetPage) {
            targetPage.classList.remove('hidden');
        }
    },

    // 显示提示消息
    showToast: function(message, type = 'info') {
        // 如果已存在toast，先移除
        const existingToast = document.getElementById('toast');
        if (existingToast) {
            existingToast.remove();
        }

        // 创建新的toast元素
        const toast = document.createElement('div');
        toast.id = 'toast';
        toast.className = `fixed bottom-4 right-4 p-4 rounded shadow-lg z-50 ${
            type === 'success' ? 'bg-green-500' :
                type === 'error' ? 'bg-red-500' :
                    type === 'warning' ? 'bg-yellow-500' : 'bg-blue-500'} text-white`;
        toast.textContent = message;

        // 添加到文档
        document.body.appendChild(toast);

        // 3秒后自动消失
        setTimeout(() => {
            toast.classList.add('opacity-0', 'transition-opacity');
            setTimeout(() => toast.remove(), 300);
        }, 3000);
    },

    // 显示确认对话框
    showConfirm: function(message, onConfirm, onCancel) {
        // 如果已存在对话框，先移除
        const existingDialog = document.getElementById('confirm-dialog');
        if (existingDialog) {
            existingDialog.remove();
        }

        // 创建新的对话框
        const dialog = document.createElement('div');
        dialog.id = 'confirm-dialog';
        dialog.className = 'fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50';

        dialog.innerHTML = `
      <div class="bg-white p-6 rounded-lg shadow-lg max-w-md">
        <h3 class="text-lg font-bold mb-4">确认</h3>
        <p class="mb-6">${message}</p>
        <div class="flex justify-end space-x-4">
          <button id="cancel-btn" class="px-4 py-2 border rounded">取消</button>
          <button id="confirm-btn" class="px-4 py-2 bg-blue-500 text-white rounded">确认</button>
        </div>
      </div>
    `;

        // 添加到文档
        document.body.appendChild(dialog);

        // 绑定事件
        document.getElementById('confirm-btn').addEventListener('click', () => {
            if (typeof onConfirm === 'function') {
                onConfirm();
            }
            dialog.remove();
        });

        document.getElementById('cancel-btn').addEventListener('click', () => {
            if (typeof onCancel === 'function') {
                onCancel();
            }
            dialog.remove();
        });
    },

    // 表单验证工具
    validateForm: function(formId, rules) {
        const form = document.getElementById(formId);
        if (!form) return false;

        let isValid = true;

        // 遍历所有验证规则
        for (const fieldId in rules) {
            const field = document.getElementById(fieldId);
            const fieldRules = rules[fieldId];
            const fieldValue = field.value.trim();

            // 检查必填
            if (fieldRules.required && !fieldValue) {
                this.showFieldError(field, fieldRules.requiredMessage || '此字段为必填项');
                isValid = false;
                continue;
            }

            // 检查最小长度
            if (fieldRules.minLength && fieldValue.length < fieldRules.minLength) {
                this.showFieldError(field, fieldRules.minLengthMessage || `长度不能小于${fieldRules.minLength}个字符`);
                isValid = false;
                continue;
            }

            // 检查最大长度
            if (fieldRules.maxLength && fieldValue.length > fieldRules.maxLength) {
                this.showFieldError(field, fieldRules.maxLengthMessage || `长度不能超过${fieldRules.maxLength}个字符`);
                isValid = false;
                continue;
            }

            // 检查正则表达式
            if (fieldRules.pattern && !fieldRules.pattern.test(fieldValue)) {
                this.showFieldError(field, fieldRules.patternMessage || '格式不正确');
                isValid = false;
                continue;
            }

            // 检查自定义验证
            if (fieldRules.validator && !fieldRules.validator(fieldValue)) {
                this.showFieldError(field, fieldRules.validatorMessage || '验证失败');
                isValid = false;
                continue;
            }

            // 清除错误提示
            this.clearFieldError(field);
        }

        return isValid;
    },

    // 显示字段错误提示
    showFieldError: function(field, message) {
        // 先清除已有的错误提示
        this.clearFieldError(field);

        // 添加错误样式
        field.classList.add('border-red-500');

        // 创建错误提示元素
        const errorElement = document.createElement('div');
        errorElement.className = 'text-red-500 text-sm mt-1 field-error';
        errorElement.textContent = message;

        // 插入到字段后面
        field.parentNode.insertBefore(errorElement, field.nextSibling);
    },

    // 清除字段错误提示
    clearFieldError: function(field) {
        // 移除错误样式
        field.classList.remove('border-red-500');

        // 移除错误提示元素
        const errorElement = field.parentNode.querySelector('.field-error');
        if (errorElement) {
            errorElement.remove();
        }
    }
};

// 导出模块
window.UIController = UIController;