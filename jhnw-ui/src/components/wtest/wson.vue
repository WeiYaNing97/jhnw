<template>
  <div>
    <input type="text" placeholder="姓名" v-if="obj.obj.typeNode === 'name'"/>
    <input type="text" placeholder="年龄" v-else/>
    <button @click="nameClick">姓名</button>
    <button @click="ageClick">年龄</button>
  </div>
</template>

<script>
    export default {
        name: "wson",
        methods:{
            nameClick(){
                emit('emitClick',{index:obj.len,typeNode:'name'})
            },
            ageClick(){
                emit('emitClick',{index:obj.len,typeNode:'age'})
            },
        }
    }
</script>

<style scoped>

</style>
