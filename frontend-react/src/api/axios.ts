import axios from 'axios';
import { BACKEND_URL } from '../util/constants';

export default axios.create({
    baseURL: BACKEND_URL
})