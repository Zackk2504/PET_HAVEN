<script setup lang="ts">
import {computed, ref} from 'vue';
import {useStore} from '~/stores/UserStores';
import {useUserStore} from '~/stores/user';
import {useI18n} from 'vue-i18n';

const {t} = useI18n();
const userStoreNe = useUserStore();
const userInfo = computed(() => userStoreNe.userInfo);
import {useToast} from 'vue-toastification';

const toast = useToast();
const userStore = useStore();
const petName = ref('');
const petWeight = ref('');
const petBreed = ref('');
const petAge = ref('');
const petGender = ref(true); // true for male, false for female
const isCat = ref(true);
const healthStatus = ref('');
const description = ref('');
const errors = ref({
  name: '',
  weight: '',
  breed: '',
  age: '',
  healthStatus: '',
  description: ''
});

function validateFields() {
  let isValid = true;
  errors.value = {name: '', weight: '', breed: '', age: '', healthStatus: '', description: ''};

  if (!petName.value) {
    errors.value.name = t('pet_name_empty');
    isValid = false;
  }
  if (!petWeight.value || isNaN(Number(petWeight.value))) {
    errors.value.weight = t('pet_weight_numeric');
    isValid = false;
  }
  if (!petBreed.value) {
    errors.value.breed = t('same_cannot_be_left_blank');
    isValid = false;
  }
  if (!petAge.value || isNaN(Number(petAge.value))) {
    errors.value.age = t('pet_age_numeric');
    isValid = false;
  }
  if (!healthStatus.value) {
    errors.value.healthStatus = t('pet_health_status_empty');
    isValid = false;
  }
  if (!description.value) {
    errors.value.description = t('description_cannot_be_empty');
    isValid = false;
  }

  return isValid;
}

function addPet() {
  if (!validateFields()) {
    return;
  }

  try {
    const newPet = {
      ten: petName.value,
      cannang: parseFloat(petWeight.value),
      giong: petBreed.value,
      tuoi: parseInt(petAge.value),
      idtaikhoan: userInfo.value.id,
      gioitinh: petGender.value,
      cophaimeokhong: isCat.value,
      tinhtrangsuckhoe: healthStatus.value,
      mota: description.value
    };
    userStore.addPet(newPet);

    // Reset form fields
    petName.value = '';
    petWeight.value = '';
    petBreed.value = '';
    petAge.value = '';
    petGender.value = true;
    isCat.value = true;
    healthStatus.value = '';
    description.value = '';

    toast.success(t('pet_added_successfully'));
    document.getElementById('exampleModal');
    return navigateTo('/user/infouser');
  } catch (error) {
    toast.error(t('add_pet_failed'));
  }
}
</script>

<template>
  <div class="card">
    <label>{{t('petName')}}:</label>
    <input type="text" v-model="petName" class="form-control"/>
    <span class="text-danger">{{ errors.name }}</span><br>

    <label>{{t('weight')}}:</label>
    <input type="text" v-model="petWeight" class="form-control"/>
    <span class="text-danger">{{ errors.weight }}</span><br>

    <label>{{t('breed')}}:</label>
    <input type="text" v-model="petBreed" class="form-control"/>
    <span class="text-danger">{{ errors.breed }}</span><br>

    <label>{{t('age')}}:</label>
    <input type="text" v-model="petAge" class="form-control"/>
    <span class="text-danger">{{ errors.age }}</span><br>

    <label>{{t('gender')}}:</label>
    <div>
      <input type="radio" id="male" v-model="petGender" :value="true">
      <label for="male">{{t('male')}}</label>
      <input type="radio" id="female" v-model="petGender" :value="false">
      <label for="female">{{t('female')}}</label>
    </div>
    <br>

    <label>{{t('petType')}}:</label>
    <div>
      <input type="radio" id="cat" v-model="isCat" :value="true">
      <label for="cat">{{t('cat')}}</label>
      <input type="radio" id="dog" v-model="isCat" :value="false">
      <label for="dog">{{t('dog')}}</label>
    </div>
    <br>

    <label>{{t('healthStatus')}}:</label>
    <textarea v-model="healthStatus" class="form-control"></textarea>
    <span class="text-danger">{{ errors.healthStatus }}</span><br>

    <label>{{t('description')}}:</label>
    <textarea v-model="description" class="form-control"></textarea>
    <span class="text-danger">{{ errors.description }}</span><br>
    <button type="button" class="custom-button" @click="addPet">{{t('addTimeSlot')}}</button>
  </div>
</template>

<style scoped>
.text-danger {
  color: red;
}
</style>