<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {useServiceStore} from '~/stores/DichVuStores';
import {useNotificationStore} from '~/stores/useNotificationStore';
import {useUserStore} from '~/stores/user';
import {useToast} from 'vue-toastification';
import InputField from '~/components/InputField.vue';
import {useForm} from 'vee-validate';
import * as yup from 'yup';

definePageMeta({
  middleware: 'auth',
});

const toast = useToast();
const serviceStore = useServiceStore();
const notificationStore = useNotificationStore();
const user = useUserStore();

const service = ref({
  tendichvu: '',
  giatien: '',
  mota: '',
  trangthai: false,
  anh: null as File | null,
});

const anh1 = ref(null);
const anh2 = ref(null);
const selectedFile = ref<File | null>(null);

const handleFileChange = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target.files && target.files.length > 0) {
    selectedFile.value = target.files[0];
    anh1.value = URL.createObjectURL(selectedFile.value);
  }
};

const schema = yup.object().shape({
  tendichvu: yup.string().required('Vui lòng nhập tên dịch vụ'),
  giatien: yup.number()
      .typeError('Giá tiền phải là một số')
      .required('Vui lòng nhập giá tiền')
      .positive('Giá tiền phải lớn hơn 0'),
  mota: yup.string().required('Vui lòng nhập mô tả')
});

const {handleSubmit, resetForm} = useForm({
  validationSchema: schema,
  initialValues: {
    tendichvu: '',
    giatien: '',
    mota: '',
    trangthai: false
  }
});
const submitForm = handleSubmit(async (formValues) => {
  await createService(formValues);
});

const submitFormUpdate = handleSubmit(async (formValues) => {
  await saveService(formValues);
});
onMounted(() => {
  serviceStore.fetchServices();
});

const cleanService = () => {
  service.value.tendichvu = '';
  service.value.giatien = '';
  service.value.mota = '';
  service.value.trangthai = false;
  anh1.value = '';
  anh2.value = '';
  resetForm()
  toast.success("Các ô input đã được làm sạch.");
};

const createService = async (formValues) => {
  try {
    const result = await serviceStore.addDichVu(formValues);
    if (result.success) {
      notificationStore.addNotification('Dịch vụ đã được tạo thành công', user.userInfo.name);
      toast.success('Dịch vụ đã được tạo thành công!');
      await serviceStore.fetchServices();
      cleanService();
      resetForm();
    } else {
      notificationStore.addNotification(`Có lỗi xảy ra khi tạo dịch vụ: ${result.message}`, user.userInfo.name);
      toast.error(`Có lỗi xảy ra khi tạo dịch vụ: ${result.message}`);
    }
  } catch (error) {
    notificationStore.addNotification('Có lỗi xảy ra khi tạo dịch vụ' + result.message, user.userInfo.name);
    toast.error('Có lỗi xảy ra khi tạo dịch vụ.', result.message);
  }
};

const saveService = async (formValues) => {
  try {
    formValues.anh = anh.value;
    const result = await serviceStore.updateDichVu(formValues);
    if (result.success) {
      notificationStore.addNotification("Dịch vụ đã được cập nhật thành công!");
      toast.success('Dịch vụ đã được lưu thành công!');
      await serviceStore.fetchServices();
      selectedFile.value = null;
      anh.value = null;
    } else {
      notificationStore.addNotification(`Có lỗi xảy ra khi cập nhật dịch vụ: ${result.message}`);
      toast.error(result.message);
    }
  } catch (error) {
    notificationStore.addNotification("Dịch vụ đã được cập nhật thất bại!", user.userInfo.name);
    console.error('Lỗi khi lưu dịch vụ:', error);
    toast.error('Có lỗi xảy ra khi lưu dịch vụ :' + error);
  }
};

const name = ref('');

const findServiceByName = async (name: string) => {
  if (!name || name.trim() === '') {
    toast.error('Tên dịch vụ không được để trống.');
    setTimeout(() => {
      serviceStore.fetchServices();
    }, 2000);
    return;
  }
  try {
    const result = await serviceStore.getDichVuByName(name);
    if (!result.status) {
      toast.error(result.message);
    }
  } catch (error) {
    toast.error('Đã xảy ra lỗi khi tìm kiếm. Vui lòng thử lại sau.');
    setTimeout(() => {
      serviceStore.fetchServices();
    }, 2000);
  }
};
const deleteService = async (serviceId) => {
  try {
    const result = await serviceStore.deleteDichVu(serviceId);

    if (result.success) {
      notificationStore.addNotification('Dịch vụ đã được xóa thành công', user.userInfo.name);
      toast.success('Dịch vụ đã được xóa thành công!');
      await serviceStore.fetchServices();
    } else {
      notificationStore.addNotification(`Có lỗi xảy ra khi xóa dịch vụ: ${result.message}`, user.userInfo.name);
      toast.error(`Có lỗi xảy ra khi xóa dịch vụ: ${result.message}`);
    }
  } catch (error) {
    notificationStore.addNotification('Có lỗi xảy ra khi xóa dịch vụ', user.userInfo.name);
    toast.error('Có lỗi xảy ra khi xóa dịch vụ.');
  }
};

const updateTTService = async (serviceId) => {
  try {
    await serviceStore.updateTTDV(serviceId);
    notificationStore.addNotification("Dịch vụ đã được cập nhật thành công!", user.userInfo.name);
    toast.success('Dịch vụ đã được cập nhật thành công!');
    await serviceStore.fetchServices();
  } catch (error) {
    notificationStore.addNotification("Dịch vụ đã được cập nhật thất bại!", user.userInfo.name);
    toast.error('Dịch vụ đã được cập nhật thất bại!');
  }
};
</script>

<template>
  <div class="container p-4">
    <div class="container">
      <div class="row">
        <div class="col-10">
          <div class="form-group">
            <div class="row">
              <div class="col-8">
                <label for="">Tìm kiếm</label>
                <input type="text" class="form-control" name="" id="" aria-describedby="helpId" placeholder=""
                       v-model="name">
              </div>
              <div class="col-2">
                <button type="button" class="custom-button" @click="findServiceByName(name)">
                  Tìm kiếm
                </button>
              </div>
              <div class="col-2">
                <button type="button" class="custom-button" @click="serviceStore.fetchServices()">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                       class="bi bi-arrow-clockwise" viewBox="0 0 16 16">
                    <path fill-rule="evenodd" d="M8 3a5 5 0 1 0 4.546 2.914.5.5 0 0 1 .908-.417A6 6 0 1 1 8 2z"/>
                    <path
                        d="M8 4.466V.534a.25.25 0 0 1 .41-.192l2.36 1.966c.12.1.12.284 0 .384L8.41 4.658A.25.25 0 0 1 8 4.466"/>
                  </svg>
                </button>
              </div>
            </div>
          </div>
        </div>
        <div class="col-2">
          <!-- Button trigger modal -->
          <button type="button" class="custom-button" data-bs-toggle="modal" data-bs-target="#modal">
            Thêm dịch vụ
          </button>
          <!-- Modal -->
          <div class="modal fade" id="modal" tabindex="-1" aria-labelledby="modalLabel" aria-hidden="true">
            <div class="modal-dialog">
              <div class="modal-content">
                <div class="modal-header">
                  <h1 class="modal-title fs-5" id="modalLabel">Thêm dịch vụ:
                    {{ service.tendichvu }}</h1>
                  <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                  <form v-on:submit.prevent="submitForm">
                    <div class="row">
                      <div class="col-6">
                        <div v-if="anh1==null">
                          <img :src="service.anh" class="card-img-top p-1" alt="Không có ảnh rùi">
                        </div>
                        <div v-else>
                          <img :src="anh1" class="card-img-top p-1" alt="Không có ảnh rùi">
                        </div>
                        <input type="file" id="fileInput1" accept="image/png, image/jpeg, image/gif"
                               @change="handleFileChange"/>
                      </div>
                      <div class="col-6">
                        <InputField type="text" name="tendichvu" class="form-control" label="Tên dịch vụ"
                                    v-model="service.tendichvu"
                                    :rules="yup.string().required('Vui lòng nhập tên dịch vụ')"/>
                        <InputField type="number" name="giatien" class="form-control" label="Giá tiền"
                                    v-model="service.giatien" :rules="yup.number().required('Vui lòng nhập giá tiền')"/>
                        <div class="form-check form-control form-check-inline">
                          <label class="form-check-label">
                            <input class="form-check-input" type="radio" name="trangthai" v-model="service.trangthai"
                                   :value="true"> Hoạt động
                            <br>
                            <input class="form-check-input" type="radio" name="trangthai" v-model="service.trangthai"
                                   :value="false"> Không hoạt động
                          </label>
                        </div>
                      </div>
                      <div class="col-12">
                        <InputField type="string" name="mota" class="form-control" label="Mô tả" v-model="service.mota"
                                    :rules="yup.string().required('Vui lòng nhập mô tả')"/>
                      </div>
                    </div>

                    <!-- Buttons -->
                    <div class="p-4">
                      <div class="row">
                        <div class="col">
                          <button type="button" class="custom-button" data-bs-dismiss="modal">Đóng</button>
                        </div>
                        <div class="col">
                          <button type="submit" class="custom-button">Thêm dịch vụ</button>
                        </div>
                        <div class="col">
                          <button type="button" class="custom-button" @click="cleanService">Clean</button>
                        </div>
                      </div>
                    </div>
                  </form>
                </div>
              </div>
            </div>
          </div>
        </div>

      </div>
    </div>
    <table class="table">
      <thead>
      <tr>
        <th scope="col">STT</th>
        <th scope="col">Tên dịch vụ</th>
        <th scope="col">Giá tiền</th>
        <th scope="col">Trạng thái</th>
        <th>Hành động</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="(service, index) in serviceStore.services" :key="service.id">
        <th scope="row">{{ index + 1 }}</th>
        <td>{{ service.tendichvu }}</td>
        <td>{{ service.giatien.toLocaleString() }} VND</td>
        <td>{{ service.trangthai == true ? 'Hoạt động' : 'Không hoạt động' }}</td>
        <td>
          <!-- Button trigger modal -->
          <div class="row">
            <div class="col">
              <button type="button" class="nav-link" @click="deleteService(service.id)" data-bs-dismiss="modal">Xóa
              </button>
            </div>
            <div class="col">
              <button type="button" class="nav-link" @click="updateTTService(service.id)" data-bs-dismiss="modal">
                <div v-if="service.trangthai">
                  Ẩn dịch vụ
                </div>
                <div v-else>
                  Hiện dịch vụ
                </div>
              </button>
            </div>
            <div class="col">
              <button type="button" class="nav-link" data-bs-toggle="modal" :data-bs-target="'#modal' + service.id">
                Chi tiết
              </button>

              <!-- Modal -->
              <div class="modal fade" :id="'modal' + service.id" tabindex="-1"
                   :aria-labelledby="'modalLabel' + service.id"
                   aria-hidden="true">
                <div class="modal-dialog">
                  <div class="modal-content">
                    <div class="modal-header">
                      <h1 class="modal-title fs-5" :id="'modalLabel' + service.id">Thông tin chi tiết dịch vụ:
                        {{ service.tendichvu }}</h1>
                      <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                      <form v-on:submit.prevent="submitFormUpdate">
                        <div class="row">
                          <div class="col-6">
                            <div v-if="anh1==null">
                              <img :src="service.anh" class="card-img-top p-1" alt="...">
                            </div>
                            <div v-else>
                              <img :src="anh1" class="card-img-top p-1" alt="...">
                            </div>
                            <input type="file" id="fileInput" accept="image/png, image/jpeg, image/gif"
                                   @change="handleFileChange"/>
                          </div>
                          <div class="col-6">
                            <input type="text" name="tendichvu" class="form-control" v-model="service.tendichvu"/>
                            <!-- Input cho giá tiền -->
                            <input type="number" name="giatien" class="form-control" v-model="service.giatien"/>
                            <label class="form-label">Trạng thái :
                              {{ service.trangthai == true ? 'Hoạt động' : 'Không hoạt động' }}</label>
                          </div>
                          <div class="col-12">
                            <input type="text" name="mota" id="mota" class="form-control" v-model="service.mota"/>
                          </div>
                        </div>
                      </form>
                    </div>

                    <div class="p-4">
                      <div class="row">
                        <div class="col">
                          <button type="button" class="custom-button" data-bs-dismiss="modal">Đóng</button>
                        </div>
                        <div class="col">
                          <button type="button" class="custom-button" @click="saveService(service)"
                                  data-bs-dismiss="modal">
                            Lưu thay đổi
                          </button>
                        </div>
                        <div class="col">
                          <button type="button" class="custom-button" @click="updateTTService(service.id)">
                            <p v-if="service.trangthai">
                              Ẩn dịch vụ
                            </p>
                            <p v-else>
                              Hiện dịch vụ
                            </p>
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.container {
  margin-top: 20px;
}

.table {
  width: 100%;
  margin: 0 auto;
}
</style>