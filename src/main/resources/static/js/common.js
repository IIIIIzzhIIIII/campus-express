/**
 * 通用前端工具
 * 依赖 jQuery
 */

// 统一 AJAX 封装，处理 Result 包裹与错误提示
function request(method, url, data) {
    return $.ajax({
        url: url,
        method: method,
        data: (method === 'GET' || method === 'DELETE') ? data : JSON.stringify(data || {}),
        contentType: 'application/json;charset=UTF-8',
        dataType: 'json'
    }).then(function (res) {
        if (res.code === 200) {
            return res.data;
        }
        if (res.code === 401) {
            // 用户端跳学生登录页，后台跳管理端登录页
            location.href = location.pathname.indexOf('/student') === 0 ? '/student/login' : '/login';
            throw new Error(res.msg || '未登录');
        }
        toast(res.msg || '操作失败', 'danger');
        throw new Error(res.msg || '操作失败');
    });
}

var api = {
    get: function (url, params) { return request('GET', url, params); },
    post: function (url, data) { return request('POST', url, data); },
    put: function (url, data) { return request('PUT', url, data); },
    del: function (url) { return request('DELETE', url); }
};

// 轻量 toast
function toast(msg, type) {
    type = type || 'success';
    var $box = $('.toast-box');
    if ($box.length === 0) {
        $box = $('<div class="toast-box"></div>').appendTo('body');
    }
    var $item = $('<div class="toast-item toast-' + type + '"></div>').text(msg);
    $box.append($item);
    setTimeout(function () { $item.fadeOut(300, function () { $item.remove(); }); }, 2500);
}

// 退出登录
function logout() {
    api.post('/api/logout').always(function () {
        location.href = '/login';
    });
}

// HTML 转义，防止 XSS
function esc(str) {
    if (str == null) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

// 快件状态 -> 标签样式
var STATUS_STYLE = {
    0: 'warning',
    1: 'primary',
    2: 'success',
    3: 'danger',
    4: 'secondary',
    5: 'dark'
};

function statusBadge(status, desc) {
    var s = STATUS_STYLE[status] || 'secondary';
    return '<span class="badge-soft text-bg-' + s + '">' + esc(desc) + '</span>';
}

// 渲染分页
function renderPager($el, page, size, total, onPage) {
    var totalPage = Math.ceil(total / size) || 1;
    var html = '<span class="text-muted">共 ' + total + ' 条</span><ul class="pagination pagination-sm mb-0">';
    html += '<li class="page-item ' + (page <= 1 ? 'disabled' : '') + '"><a class="page-link" href="javascript:;" data-p="' + (page - 1) + '">上一页</a></li>';
    for (var i = 1; i <= totalPage; i++) {
        if (totalPage > 9 && Math.abs(i - page) > 4 && i !== 1 && i !== totalPage) {
            if (i === 2 || i === totalPage - 1) html += '<li class="page-item disabled"><span class="page-link">…</span></li>';
            continue;
        }
        html += '<li class="page-item ' + (i === page ? 'active' : '') + '"><a class="page-link" href="javascript:;" data-p="' + i + '">' + i + '</a></li>';
    }
    html += '<li class="page-item ' + (page >= totalPage ? 'disabled' : '') + '"><a class="page-link" href="javascript:;" data-p="' + (page + 1) + '">下一页</a></li>';
    html += '</ul>';
    $el.html(html);
    $el.find('.page-link').on('click', function () {
        var p = parseInt($(this).attr('data-p'));
        if (!p || p < 1 || p > totalPage || p === page) return;
        onPage(p);
    });
}

// 侧边栏移动端切换
function toggleSidebar() {
    $('.sidebar').toggleClass('show');
}

// ===================== 快递业务扩展（用户端 / 寄件 / 存件） =====================

// 寄件物流状态
var SEND_STATUS_DESC = {0: '待揽收', 1: '已揽收', 2: '运输中', 3: '派送中', 4: '已签收', 5: '已取消'};
var SEND_STATUS_STYLE = {0: 'warning', 1: 'primary', 2: 'info', 3: 'primary', 4: 'success', 5: 'dark'};

function sendStatusBadge(status) {
    var s = SEND_STATUS_STYLE[status] || 'secondary';
    var d = SEND_STATUS_DESC[status] || '未知';
    return '<span class="badge-soft text-bg-' + s + '">' + d + '</span>';
}

// 存件（寄存）状态
var DEPOSIT_STATUS_DESC = {0: '待到站', 1: '在库', 2: '已取出', 3: '已取消'};
var DEPOSIT_STATUS_STYLE = {0: 'warning', 1: 'primary', 2: 'success', 3: 'dark'};

function depositStatusBadge(status) {
    var s = DEPOSIT_STATUS_STYLE[status] || 'secondary';
    var d = DEPOSIT_STATUS_DESC[status] || '未知';
    return '<span class="badge-soft text-bg-' + s + '">' + d + '</span>';
}

// 学生端快件卡片（待取件时突出取件码）
function packageCardHtml(p) {
    var html = '<div class="border rounded p-3 mb-3">'
        + '<div class="d-flex justify-content-between align-items-center mb-2">'
        + '<span class="fw-bold">' + esc(p.companyName || '快递') + '</span>'
        + statusBadge(p.status, p.statusDesc)
        + '</div>'
        + '<div class="small text-muted">快递单号：' + esc(p.trackingNo) + '</div>'
        + '<div class="small text-muted">收件人：' + esc(p.receiverName) + ' ' + esc(p.receiverPhone) + '</div>'
        + '<div class="small text-muted">入库时间：' + esc(p.inTime) + '</div>';
    if (p.status === 1 || p.status === 3) {
        html += '<div class="mt-2 p-2 bg-light rounded text-center">'
            + '<div class="text-muted small">取件码</div>'
            + '<div class="fs-3 fw-bold text-primary">' + esc(p.pickupCode) + '</div>'
            + '<div class="text-muted small">货架号：' + esc(p.shelfNo) + '，请凭取件码到驿站取件</div>'
            + '</div>';
    }
    if (p.status === 2) {
        html += '<div class="small text-muted mt-1">取件时间：' + esc(p.pickupTime) + '</div>';
    }
    return html + '</div>';
}

// 学生自助查询结果卡片：兼容「驿站入库快件(PACKAGE)」与「别人寄给我的寄件订单(SEND)」
function studentQueryCardHtml(item) {
    if (!item || item.type !== 'SEND') {
        return packageCardHtml(item);
    }
    var html = '<div class="border rounded p-3 mb-3">'
        + '<div class="d-flex justify-content-between align-items-center mb-2">'
        + '<span class="fw-bold">' + esc(item.companyName || '快递')
        + ' <span class="badge text-bg-light border">寄给我的包裹</span></span>'
        + sendStatusBadge(item.status)
        + '</div>'
        + '<div class="small text-muted">寄件单号：' + esc(item.orderNo) + '</div>'
        + '<div class="small text-muted">运单号：' + esc(item.trackingNo || '待驿站揽收后生成') + '</div>'
        + '<div class="small text-muted">寄件人：' + esc(item.senderName || '-') + '</div>'
        + '<div class="small text-muted">收件人：' + esc(item.receiverName) + ' ' + esc(item.receiverPhone) + '</div>'
        + '<div class="small text-muted">物品：' + esc(item.itemName || '-') + '</div>'
        + '<div class="small text-muted">下单时间：' + esc(item.time) + '</div>';
    if (item.status === 0) {
        html += '<div class="small text-muted mt-1">寄件人还未把包裹交给驿站，驿站揽收后即可跟踪物流</div>';
    }
    return html + '</div>';
}

// datetime-local 值（2026-09-14T10:30）-> 后端可解析的 yyyy-MM-dd HH:mm:ss
function toTimeStr(value) {
    if (!value) return '';
    var v = String(value).replace('T', ' ');
    return v.length === 16 ? v + ':00' : v;
}

// 渲染物流轨迹时间轴
function renderTimeline($el, logs) {
    var html = '';
    (logs || []).forEach(function (l) {
        html += '<li class="position-relative ps-3 pb-3 border-start">'
            + '<div class="small fw-bold">' + esc(l.content) + '</div>'
            + '<div class="text-muted small">'
            + (l.location ? esc(l.location) + ' · ' : '')
            + esc(l.operator || '系统') + ' · ' + esc(l.createTime) + '</div>'
            + '</li>';
    });
    $el.html(html || '<li class="text-muted small">暂无物流信息</li>');
}
