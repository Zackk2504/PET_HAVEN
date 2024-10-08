const API_BASE_URL = 'http://localhost:8080/api';
//khai báo các đường dấn dịch vụ theo yêu cầu
const API_ENDPOINTS = {
    dichVu: {
        getAll:`${API_BASE_URL}/dich-vu/all`,
        addDichVu:`${API_BASE_URL}/dich-vu/add`,
        updateDichVu:`${API_BASE_URL}/dich-vu/update`,
        deleteDichVu:`${API_BASE_URL}/dich-vu/delete`,
        getDichVuByName:`${API_BASE_URL}/dich-vu/find`
    }
};

export default { API_BASE_URL, API_ENDPOINTS };