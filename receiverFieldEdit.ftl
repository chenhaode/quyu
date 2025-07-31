<#include "../../public/head.ftl">

<div class="row border-bottom white-bg hlp-tags-view">
    <div class="col-sm-12 col-md-12 col-lg-12">
        <ol class="breadcrumb">
            <#include "../../public/breadcrumb.ftl">
            <strong>境外付款 / 收款人字典管理 / 编辑字段</strong>
            </li>
        </ol>
    </div>
</div>

<div id="app">
    <el-form ref="form" :model="form" label-width="120px"
             style="margin-top: 20px; margin-botton: 400px; width: 40%" id="form" :rules="rules">

        <el-form-item label="字段名称" prop="fieldName">
            <el-input v-model="form.fieldName" placeholder="请输入字段名称" id="fieldName"></el-input>
        </el-form-item>

        <el-form-item label="接口字段名称" prop="interfaceFieldName">
            <el-input v-model="form.interfaceFieldName" placeholder="请输入接口字段名称" id="interfaceFieldName"></el-input>
        </el-form-item>

        <el-form-item label="字段类型" prop="fieldType">
            <el-select v-model="form.fieldType" placeholder="请选择字段类型" id="fieldType">
                <#if fieldTypeList??>
                    <#list fieldTypeList as fieldType>
                        <el-option value="${fieldType.name()?if_exists}" label="${fieldType.desc}"></el-option>
                    </#list>
                </#if>
            </el-select>
        </el-form-item>

        <el-form-item label="选项值" prop="rangeValue">
            <el-input v-model="form.rangeValue" placeholder="请输入选项值" id="rangeValue"></el-input>
        </el-form-item>

        <el-form-item label="是否必填" prop="required">
            <el-radio label="true" v-model="form.required">是</el-radio>
            <el-radio label="false" v-model="form.required">否</el-radio>
        </el-form-item>

        <el-form-item label="录入提示" prop="inputPrompt">
            <el-input v-model="form.inputPrompt" placeholder="请输入录入提示" id="inputPrompt"></el-input>
        </el-form-item>

        <el-form-item label="字段排序" prop="sort">
            <el-input v-model="form.sort" placeholder="请输入字段排序" id="sort" type="number"></el-input>
        </el-form-item>

        <el-form-item label="是否启用" prop="activeState">
            <#if statusList??>
                <#list statusList as status>
                    <el-radio label="${status.name()?if_exists}" v-model="form.activeState">${status.desc}</el-radio>
                </#list>
            </#if>
        </el-form-item>

        <el-form-item label="备注" prop="remark">
            <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" id="remark"></el-input>
        </el-form-item>

        <el-form-item>
            <el-button type="primary" id="btn" @click="updateReceiverField()">确认</el-button>
            <el-button onclick="javascript:history.back(-1);">取消</el-button>
        </el-form-item>
    </el-form>
</div>

<script type="text/javascript">
    var app = new Vue({
        el: '#app',
        data() {
            return {
                form: {
                    id: '',
                    fieldName: '',
                    interfaceFieldName: '',
                    fieldType: '',
                    rangeValue: '',
                    required: 'false',
                    inputPrompt: '',
                    sort: '',
                    activeState: 'ENABLE',
                    remark: ''
                },

                rules: {
                    fieldName: [
                        {required: true, message: '请输入字段名称', trigger: 'blur'},
                    ],
                    fieldType: [
                        {required: true, message: '请选择字段类型', trigger: 'blur'},
                    ]
                }
            }
        },
        methods: {
            // 数据初始化
            init: function () {
                <#if entity.id??>
                    this.form.id = "${entity.id?if_exists}";
                </#if>
                <#if entity.fieldName??>
                    this.form.fieldName = "${entity.fieldName?if_exists}";
                </#if>
                <#if entity.interfaceFieldName??>
                    this.form.interfaceFieldName = "${entity.interfaceFieldName?if_exists}";
                </#if>
                <#if entity.fieldType??>
                    this.form.fieldType = "${entity.fieldType?if_exists}";
                </#if>
                <#if entity.rangeValue??>
                    this.form.rangeValue = "${entity.rangeValue?if_exists}";
                </#if>
                <#if entity.required??>
                    this.form.required = "${entity.required?string('true', 'false')}";
                </#if>
                <#if entity.inputPrompt??>
                    this.form.inputPrompt = "${entity.inputPrompt?if_exists}";
                </#if>
                <#if entity.sort??>
                    this.form.sort = "${entity.sort?if_exists}";
                </#if>
                <#if entity.activeState??>
                    this.form.activeState = "${entity.activeState?if_exists}";
                </#if>
                <#if entity.remark??>
                    this.form.remark = "${entity.remark?if_exists}";
                </#if>
            },
            updateReceiverField: function() {
                var isValid = true;
                this.$refs['form'].validate((valid) => {
                    if (!valid) {
                        isValid = false;
                        return false;
                    }
                });
                if(!isValid) {
                    return;
                }

                // 处理required字段，转换为boolean
                var formData = Object.assign({}, this.form);
                formData.required = this.form.required === 'true';

                $.post("${ctxPath}/receiverField/doUpdateReceiverField", formData, function (result) {
                    console.log(result);
                    if (result.code == 200) {
                        alert(result.msg || '修改成功');
                        window.location.href = "${ctxPath}/receiverField/receiverFieldList";
                    } else {
                        alert(result.msg || '修改失败');
                    }
                }, "json");
            }
        }
    })

    $(document).ready(function () {
        app.init();
    })
</script>

<#include "../../public/footer.ftl">